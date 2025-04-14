import os
import numpy as np
from PIL import Image, ImageOps, ImageEnhance
import tensorflow as tf
from tensorflow.keras.models import Sequential
from tensorflow.keras.layers import GlobalAveragePooling2D, Dense, Dropout, BatchNormalization
from tensorflow.keras.optimizers import Adam
from tensorflow.keras.preprocessing.image import ImageDataGenerator
from tensorflow.keras.callbacks import EarlyStopping, ModelCheckpoint, ReduceLROnPlateau, TensorBoard
from tensorflow.keras.applications import EfficientNetB0
from tensorflow.keras import regularizers
from collections import Counter
import matplotlib.pyplot as plt
from skimage.metrics import structural_similarity as ssim
import pandas as pd
from scipy.ndimage import laplace

# ====================== ENHANCED CONFIGURATION ======================
INPUT_FOLDER = "dataset"
MODEL_PATH = "improved_disease_model.h5"
IMG_SIZE = (300, 300)
BATCH_SIZE = 16
EPOCHS = 120
CLASS_NAMES = sorted(['Acne', 'Carcinoma', 'Eczema', 'Keratosis', 'Milia', 'Rosacea'])
NUM_CLASSES = len(CLASS_NAMES)

# ====================== IMAGE QUALITY ASSESSMENT ======================
def assess_image_quality(img_path):
    try:
        img = Image.open(img_path).convert('L')  # Convert to grayscale
        img_array = np.array(img)
        
        # Blur detection (Laplacian variance)
        blur = laplace(img_array).var()
        
        # Contrast assessment
        contrast = np.std(img_array)
        
        # Brightness check
        brightness = np.mean(img_array)
        
        # Structural similarity (SSIM with uniform thresholding)
        _, ref = np.histogram(img_array, bins=256, range=(0, 255))
        similarity = ssim(img_array, ref, data_range=255)
        
        is_acceptable = (blur > 100 and contrast > 30 and 50 <= brightness <= 200 and similarity > 0.6)
        
        return is_acceptable, {"blur": blur, "contrast": contrast, "brightness": brightness, "similarity": similarity}
    except Exception as e:
        return False, {"error": str(e)}

# ====================== DATA GENERATION ======================
def create_generators():
    train_datagen = ImageDataGenerator(
        preprocessing_function=lambda x: np.array(ImageOps.equalize(Image.fromarray((x*255).astype('uint8')))) / 255.0,
        rotation_range=45,
        width_shift_range=0.25,
        height_shift_range=0.25,
        shear_range=0.2,
        zoom_range=0.3,
        horizontal_flip=True,
        vertical_flip=True,
        brightness_range=[0.7, 1.4],
        fill_mode='reflect',
        validation_split=0.15
    )

    train_generator = train_datagen.flow_from_directory(
        INPUT_FOLDER,
        target_size=IMG_SIZE,
        batch_size=BATCH_SIZE,
        class_mode='categorical',
        subset='training',
        classes=CLASS_NAMES,
        shuffle=True
    )

    val_generator = train_datagen.flow_from_directory(
        INPUT_FOLDER,
        target_size=IMG_SIZE,
        batch_size=BATCH_SIZE,
        class_mode='categorical',
        subset='validation',
        classes=CLASS_NAMES
    )

    return train_generator, val_generator

# ====================== MODEL ARCHITECTURE ======================
def build_model():
    base_model = EfficientNetB0(weights='imagenet', include_top=False, input_shape=(IMG_SIZE[0], IMG_SIZE[1], 3))
    base_model.trainable = True
    for layer in base_model.layers[:100]:
        layer.trainable = False

    model = Sequential([
        base_model,
        GlobalAveragePooling2D(),
        Dense(512, activation='relu', kernel_regularizer=regularizers.l1_l2(l1=1e-5, l2=1e-4)),
        BatchNormalization(),
        Dropout(0.6),
        Dense(256, activation='relu', kernel_regularizer=regularizers.l1_l2(l1=1e-5, l2=1e-4)),
        BatchNormalization(),
        Dropout(0.5),
        Dense(NUM_CLASSES, activation='softmax')
    ])

    model.compile(
        optimizer=Adam(learning_rate=1e-5),
        loss='categorical_crossentropy',
        metrics=['accuracy', tf.keras.metrics.AUC(name='auc')]
    )
    return model

# ====================== TRAINING ======================
def train():
    train_gen, val_gen = create_generators()
    
    class_counts = Counter(train_gen.classes)
    total = sum(class_counts.values())
    class_weights = {i: total / (count * NUM_CLASSES) for i, count in enumerate(class_counts.values())}
    
    model = build_model()
    
    callbacks = [
        EarlyStopping(patience=15, monitor='val_auc', mode='max', restore_best_weights=True),
        ModelCheckpoint(MODEL_PATH, monitor='val_auc', mode='max', save_best_only=True),
        ReduceLROnPlateau(monitor='val_auc', factor=0.5, patience=5, min_lr=1e-7),
        TensorBoard(log_dir='./logs')
    ]

    history = model.fit(
        train_gen,
        steps_per_epoch=train_gen.samples // BATCH_SIZE,
        validation_data=val_gen,
        validation_steps=val_gen.samples // BATCH_SIZE,
        epochs=EPOCHS,
        callbacks=callbacks,
        class_weight=class_weights
    )

    plot_training_metrics(history)
    return model

# ====================== TRAINING METRICS ======================
def plot_training_metrics(history):
    plt.figure(figsize=(18, 6))
    metrics = ['accuracy', 'loss', 'auc']
    for i, metric in enumerate(metrics):
        plt.subplot(1, 3, i+1)
        plt.plot(history.history[metric], label='Train')
        plt.plot(history.history[f'val_{metric}'], label='Validation')
        plt.title(metric.capitalize())
        plt.legend()
    plt.savefig('training_metrics.png')

if __name__ == "__main__":
    print("🚀 Starting enhanced training pipeline...")
    trained_model = train()
    print(f"\n✅ Best model saved to {MODEL_PATH}")
