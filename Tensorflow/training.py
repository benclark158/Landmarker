import glob
import tensorflow as tf
from keras.preprocessing.image import ImageDataGenerator
from keras.utils import Sequence
from time import time
from tensorflow.python.keras.callbacks import TensorBoard

import sys
import keras
import functools

import pandas as pd
import numpy as np
from sklearn.model_selection import train_test_split
from keras.utils import to_categorical
from keras.layers import Input
import cv2 as cv
from PIL import ImageFile
import array as arr

from datetime import datetime

import progressbar

import random


def trainingImg(model, numClasses=2, noEpochs=10):
    #train_datagen = ImageDataGenerator()

    #test_datagen = ImageDataGenerator()

    tensorboard = TensorBoard(log_dir="logs\{}".format(time()))

    train_datagen = ImageDataGenerator(
        rescale = 1./255,
        rotation_range=45,
        width_shift_range=0.15,
        height_shift_range=0.15,
        horizontal_flip=True,
        zoom_range=0.5,
        shear_range = 0.2,
        )

    val_datagen = ImageDataGenerator(
        rescale = 1./255
        )

    training_set = train_datagen.flow_from_directory(
        'C:\\Users\\Ben Clark\\Desktop\\TrainingData\\GoogleSmall\\Training\\',
        target_size=(224, 224),
        batch_size=32,
        class_mode="sparse",
        shuffle=True,
    )

    val_set = val_datagen.flow_from_directory(
        'C:\\Users\\Ben Clark\\Desktop\\TrainingData\\GoogleSmall\\Validation\\',
        target_size=(224, 224),
        batch_size=32,
        class_mode="sparse"
    )

  
    model.fit(
        training_set,
        #steps_per_epoch=74,
        epochs=noEpochs,
        #validation_split=0.1,
        validation_data=val_set,
        #validation_steps=100,
        callbacks=[tensorboard]
    )

    print("\n\nMaking Prediction")

    img = cv.imread("C:/Users/Ben Clark/Desktop/testimg.jpg") / 255
    img = np.reshape(img, (1, 224, 224, 3))

    output = model.predict(img)

    print(output)

    print(model.evaluate(
        training_set,
        steps=166
    ))

    return model

def loadImages(paths, total):
    ImageFile.LOAD_TRUNCATED_IMAGES = True

    now = datetime.now()

    current_time = now.strftime("%H:%M:%S")
    print("Current Time =", current_time)

    images = []
  
    bar = progressbar.ProgressBar(maxval=total, \
        widgets=[progressbar.Bar('=', '[', ']'), ' ', progressbar.Percentage()])
    bar.start()
    i = 0

    for fp in paths:
        path = "C:/Users/Ben Clark/Desktop/" + fp[0]
        img = cv.imread(path)
        if(img.shape == (224, 224, 3)):
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


def training(model, numClasses, noEpochs=10, index=99):
    attList = ["landmarkID", "url", "noise_lat", "noise_long"]
    #attDF = pd.read_csv('C:\\Users\\Ben Clark\\Desktop\\partitionedDataset\\partition_' + str(index) + '.csv', usecols=attList, dtype=str)
    attDF = pd.read_csv('C:\\Users\\Ben Clark\\Desktop\\lim-formattedData.csv', usecols=attList, dtype=str)

    img_rows, img_cols = 244, 244
    batch_size = 32

    labels = attDF['landmarkID'].values

    x_train, x_test, y_train, y_test = train_test_split(attDF, labels, test_size = 0.1)

    #x is inputs 
    # y is labels

    x_train_img = x_train.iloc[:, :2]
    x_test_img = x_test.iloc[:, :2]

    x_train_gps = x_train.iloc[:, 2:]
    x_test_gps = x_test.iloc[:, 2:]


    y_train = [int(numeric_string) for numeric_string in y_train]
    y_test = [int(numeric_string) for numeric_string in y_test]

    print("-- Finished splitting")
    print("-- Loading images")

    now = datetime.now()

    current_time = now.strftime("%H:%M:%S")

    print("-- Starting Training @ ", current_time)

    tensorboard = TensorBoard(log_dir="logs\\200epochs-{}".format(time()))

    trainGen = doTrain(dataframe=x_train_img, gps=x_train_gps, labels=y_train, batch_size=batch_size)
    testGen = doTrain(dataframe=x_test_img, gps=x_test_gps, labels=y_test, batch_size=batch_size)

    trainSteps = int(len(y_train) / batch_size)
    testSteps = int(len(y_test) / batch_size)

    model.fit(
        trainGen,
        steps_per_epoch=trainSteps,
        epochs=noEpochs,
        callbacks=[tensorboard],
        validation_data=testGen,
        validation_steps=testSteps)

    return model


def doTrain(dataframe, gps, labels, batch_size=32):

    # suffled indices    
    #idx = np.random.permutation( X.shape[0])

    y_labs = np.reshape(labels, (len(gps.values), 1))
    x_gps = gps.astype(np.float).values

    bs = batch_size

    datagen = ImageDataGenerator(
            rescale = 1./255,
            rotation_range=20,
            width_shift_range=0.15,
            height_shift_range=0.15,
            horizontal_flip=False,
            zoom_range=0.5,
            shear_range = 0.2,
            )

    batches = datagen.flow_from_dataframe(
            dataframe=dataframe,
            x_col="url", 
            y_col="landmarkID", 
            target_size=(224, 224), 
            batch_size=bs,
            shuffle=False,
            class_mode="sparse")


    i = 0

    while i == 0:
        i = 0

        idx0 = 0
        for batch in batches:
            idx1 = idx0 + batch[0].shape[0]

            yield [x_gps[idx0:idx1], batch[0]], batch[1] # Images and GPS
            #yield batch[0], batch[1] # Just Images

            idx0 = idx1
            if(idx0 >= len(gps.values)):
                break

def makePrediction(model, imgPath):
  img = cv.imread(imgPath) / 255
  img = np.reshape(img, (1, 224, 224, 3))

  res = model.predict(img)
  return res