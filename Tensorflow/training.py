import glob
import tensorflow as tf
from keras.preprocessing.image import ImageDataGenerator
from time import time
from tensorflow.python.keras.callbacks import TensorBoard

def training(model, steps, noEpochs):
    #train_datagen = ImageDataGenerator()

    #test_datagen = ImageDataGenerator()

    tensorboard = TensorBoard(log_dir="logs\{}".format(time()))

    train_datagen = ImageDataGenerator(shear_range = 0.2, horizontal_flip = True)

    test_datagen = ImageDataGenerator(rescale = 1./255)

    training_set = train_datagen.flow_from_directory(
        'E:\\Dissertation\\Landmarker\\Training\\TrainingData\\UoNDataset\\Jubilee',
        target_size=(244, 244),
        batch_size=12,
        class_mode='binary'
    )

    testing_set = test_datagen.flow_from_directory(
        'E:\\Dissertation\\Landmarker\\Training\\TrainingData\\UoNDataset\\Jubilee',
        target_size=(244, 244),
        batch_size=12,
        class_mode='binary'
    )

    model.fit(
        training_set,
        steps_per_epoch=steps,
        epochs=noEpochs,
        validation_data=testing_set,
        validation_steps=100,
        callbacks=[tensorboard]
    )

    return model
