import glob
import tensorflow as tf
from keras.preprocessing.image import ImageDataGenerator
from time import time
from tensorflow.python.keras.callbacks import TensorBoard

import keras

import pandas as pd
import numpy as np
from sklearn.model_selection import train_test_split
from keras.utils import to_categorical
from keras.layers import Input
from skimage.io import imread

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
        [training_set],
        steps_per_epoch=steps,
        epochs=noEpochs,
        #validation_data=testing_set,
        #validation_steps=100,
        callbacks=[tensorboard]
    )

    return model

def loadImages(paths):
    images = []
  
    for fp in paths:
        images.append(imread("" + fp[0]))
  
    images = np.asarray(images, dtype=np.float32)
  
    #Normalise pixel values
    images = images / 255.0

    images = images.reshape(images.shape[0], 244, 244, 3)

    return images


def training(model, steps, noEpochs, numClasses):
    colList = ["landmarkID", "url", "actual_latitude", "actual_longitude", "noise_lat", "noise_long"]
    attList = ["url", "noise_lat", "noise_long"]
    dataDF = pd.read_csv('..\\Training\\formattedData.csv', usecols=colList)
    attDF = pd.read_csv('..\\Training\\formattedData.csv', usecols=attList)

    img_rows, img_cols = 244, 244

    batch_size = 32
    #input_shape = (img_rows, img_cols, 3)

    #print(train)

    labels = dataDF['landmarkID'].values
    labels = keras.utils.to_categorical(labels, numClasses)

    x_train, x_test, y_train, y_test = train_test_split(attDF, labels, test_size = 0.3, random_state=666)

    #x is inputs 
    # y is labels

    x_train_img = x_train.iloc[:, :1]
    x_test_img = x_test.iloc[:, :1]

    x_train_gps = x_train.iloc[:,1:]
    x_test_gps = x_test.iloc[:,1:]


    x_train_gps_lat = x_train.iloc[:,1]
    x_train_gps_long = x_train.iloc[:,2]

    x_test_gps_lat = x_test.iloc[:,1]
    x_test_gps_long = x_test.iloc[:,2]

    #print(x_train_gps.shape)
    #print(x_test_gps.shape)

    print("-- Finished splitting")

    #x_train_img = np.hstack(x_train_img)
    #x_train_gps = [np.hstack(x_train_gps_lat), np.hstack(x_train_gps_long)]
    #x_test_img = np.hstack(x_test_img)
    #x_test_gps = [np.hstack(x_test_gps_lat), np.hstack(x_test_gps_long)]

    #dataVar_tensor = tf.constant(x_train_gps.values, dtype = tf.float32, shape=[2,235126])
    #dataVar_tensor1 = tf.constant(x_test_gps.values, dtype = tf.float32, shape=[2,100769])


    #print(x_train_gps)
    #print(x_train_gps)
    #print(x_train_gps.shape)
    
    #print("\n\n\n\n\n\n")

    #print(x_test_img.shape)
    #print(x_test_gps.shape)

    print("-- Loading images")

    #trainDS = tf.constant(np.array([x_train_img.values, x_train_gps.values]), shape=[3,235126], dtype = tf.float32)
    #testDS = tf.constant(np.hstack([x_test_img.values, x_test_gps.values]), shape=[3,100769], dtype = tf.float32)

    #print(x_train_img)
    #print(x_train_img.values)

    train_images = loadImages(x_train_img.values)
    test_images = loadImages(x_test_img.values)

    

    print("-- Starting Training")


    model.fit([x_train_gps, train_images], y_train,
          batch_size=batch_size,
          epochs=noEpochs,
          #callbacks=callbacks,
          verbose=1,
          validation_data=([x_test_gps, test_images], y_test),
          shuffle=True)


