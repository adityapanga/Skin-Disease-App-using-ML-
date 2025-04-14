from flask import Flask, request, jsonify
import tensorflow as tf
import numpy as np
from PIL import Image
import io

app = Flask(__name__)
model = tf.keras.models.load_model('disease_detection_model.h5')

# Define class names (update with your actual classes)
CLASS_NAMES = ['Acne', 'Carcinoma', 'Eczema', 'Keratosis', 'Milia', 'Rosacea']

@app.route('/predict', methods=['POST'])
def predict():
    if 'file' not in request.files:
        return jsonify({'error': 'No file uploaded'}), 400
    
    file = request.files['file'].read()
    image = Image.open(io.BytesIO(file))
    
    # Preprocess image (adjust based on your model's requirements)
    image = image.resize((224, 224))  # example size
    image = np.array(image) / 255.0
    image = np.expand_dims(image, axis=0)
    
    predictions = model.predict(image)
    predicted_class = CLASS_NAMES[np.argmax(predictions)]
    confidence = float(np.max(predictions))
    
    return jsonify({
        'disease': predicted_class,
        'confidence': confidence
    })

if __name__ == '__main__':
    app.run(host='0.0.0.0', port=5000)