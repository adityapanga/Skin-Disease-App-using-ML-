import os
import json
import numpy as np
from PIL import Image, ImageOps, ImageEnhance
import tensorflow as tf
from tensorflow.keras.models import load_model
import matplotlib.pyplot as plt

# Configuration 
MODEL_PATH = "disease_detection_model.h5"
CLASS_NAMES_PATH = "class_names.json"
IMG_SIZE = (224, 224)

class SkinDiseasePredictor:
    def __init__(self):
        """Load the trained model and class names"""
        try:
            self.model = load_model(MODEL_PATH)
            _ = self.model.predict(np.zeros((1, *IMG_SIZE, 3)))  # Warm-up
            print("✅ Model loaded and initialized successfully")
        except Exception as e:
            print(f"❌ Failed to load model: {e}")
            raise
        
        try:
            with open(CLASS_NAMES_PATH, 'r') as f:
                self.class_names = json.load(f)
            print(f"📂 Loaded {len(self.class_names)} classes from '{CLASS_NAMES_PATH}'")
        except Exception as e:
            print(f"❌ Failed to load class names: {e}")
            raise

    def preprocess_image(self, image_path):
        """Prepare medical image for prediction"""
        try:
            img = Image.open(image_path).convert('RGB')
            img = ImageOps.autocontrast(img, cutoff=3)
            img = ImageEnhance.Sharpness(img).enhance(1.5)
            img.thumbnail((IMG_SIZE[0]*2, IMG_SIZE[1]*2), Image.LANCZOS)
            img = img.resize(IMG_SIZE, Image.LANCZOS)
            img_array = np.array(img) / 255.0
            return np.expand_dims(img_array, axis=0)
        except Exception as e:
            print(f"❌ Image processing failed: {e}")
            return None

    def predict(self, image_path, top_n=3):
        """Make and visualize prediction"""
        processed_img = self.preprocess_image(image_path)
        if processed_img is None:
            return None

        predictions = self.model.predict(processed_img)[0]
        sorted_indices = np.argsort(predictions)[::-1]

        # Visualization
        plt.figure(figsize=(15, 6))

        plt.subplot(1, 2, 1)
        original_img = Image.open(image_path)
        plt.imshow(original_img)
        plt.title("Input Image")
        plt.axis('off')

        plt.subplot(1, 2, 2)
        colors = ['#2ecc71' if i == sorted_indices[0] else '#3498db' for i in range(len(predictions))]
        bars = plt.barh(self.class_names, predictions, color=colors)
        plt.xlim(0, 1)
        plt.title("Diagnosis Confidence")

        for bar, prob in zip(bars, predictions):
            width = bar.get_width()
            plt.text(width + 0.02, bar.get_y() + bar.get_height()/2,
                     f"{prob*100:.1f}%", va='center')

        plt.tight_layout()
        plt.savefig('diagnosis_report.png')
        plt.show()

        if predictions[sorted_indices[0]] < 0.2:
            print("⚠️ Low confidence in prediction — consider using a higher-quality image.")

        return {
            'top_prediction': {
                'class': self.class_names[sorted_indices[0]],
                'confidence': float(predictions[sorted_indices[0]])
            },
            'all_predictions': [
                {'class': self.class_names[i], 'confidence': float(predictions[i])}
                for i in sorted_indices[:top_n]
            ]
        }

if __name__ == "__main__":
    predictor = SkinDiseasePredictor()
    
    test_image = "test_sample.jpg"  # Replace with your test image
    if os.path.exists(test_image):
        print(f"\n🔍 Analyzing {test_image}...")
        results = predictor.predict(test_image)
        
        if results:
            print("\nDIAGNOSIS REPORT:")
            print(f"Most likely: {results['top_prediction']['class']} "
                  f"({results['top_prediction']['confidence']*100:.1f}% confidence)")

            print("\nDetailed predictions:")
            for pred in results['all_predictions']:
                print(f"- {pred['class']}: {pred['confidence']*100:.1f}%")

            print("\n📊 Visualization saved to 'diagnosis_report.png'")
    else:
        print(f"❌ Error: Test image '{test_image}' not found")
        print("Current directory contains:", os.listdir('.'))
