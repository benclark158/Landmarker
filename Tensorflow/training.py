import glob
import tensorflow as tf
from keras.preprocessing.image import ImageDataGenerator
from time import time
from tensorflow.python.keras.callbacks import TensorBoard

import sys
import keras

import pandas as pd
import numpy as np
from sklearn.model_selection import train_test_split
from keras.utils import to_categorical
from keras.layers import Input
import cv2 as cv
from PIL import ImageFile

import progressbar

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

def loadImages(paths, total):
    ImageFile.LOAD_TRUNCATED_IMAGES = True

    images = []
  
    bar = progressbar.ProgressBar(maxval=total, \
        widgets=[progressbar.Bar('=', '[', ']'), ' ', progressbar.Percentage()])
    bar.start()
    i = 0

    for fp in paths:
        path = "E:/Dissertation/Landmarker/Training" + fp[0]
        img = cv.imread(path)
        if(img.shape == (244, 244, 3)):
            images.append(img)
            i = i + 1
            bar.update(i)
        else :
            print("NAME: " + path + " -- shape: ")
            print(img.shape)
  
    bar.finish()

    #print(images.shape)
    images = np.array(images, dtype=np.float32)
  
    #Normalise pixel values
    images = images / 255.0

    images = images.reshape(images.shape[0], 224, 224, 3)

    return images


def training(model, steps, noEpochs, numClasses):
    colList = ["landmarkID", "url", "actual_latitude", "actual_longitude", "noise_lat", "noise_long"]
    attList = ["url", "noise_lat", "noise_long"]
    dataDF = pd.read_csv('..\\Training\\formattedDataPrt.csv', usecols=colList)
    attDF = pd.read_csv('..\\Training\\formattedDataPrt.csv', usecols=attList)

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

    print("-- Finished splitting")
    print("-- Loading images")

    train_images = loadImages(x_train_img.values, len(x_train_img.values))
    test_images = loadImages(x_test_img.values, len(x_test_img.values))

    

    print("-- Starting Training")

    tensorboard = TensorBoard(log_dir="logs\{}".format(time()))

    model.fit([x_train_gps, train_images], y_train,
          batch_size=batch_size,
          epochs=noEpochs,
          callbacks=[tensorboard],
          verbose=1,
          validation_data=([x_test_gps, test_images], y_test),
          shuffle=True)


