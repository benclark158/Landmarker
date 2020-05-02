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

def training(model, numClasses, noEpochs=10, index=99):
    #loads dataframe from csv as string values
    attList = ["landmarkID", "url", "noise_lat", "noise_long"]
    attDF = pd.read_csv('C:\\Users\\Ben Clark\\Desktop\\lim-formattedData.csv', usecols=attList, dtype=str)

    #sets img sizes and batch sizee
    img_rows, img_cols = 244, 244
    batch_size = 32

    #gets the labels
    labels = attDF['landmarkID'].values

    #splits into training and validation sets
    x_train, x_test, y_train, y_test = train_test_split(attDF, labels, test_size = 0.1)

    #x is inputs 
    #y is labels

    #splits into img and gps data
    x_train_img = x_train.iloc[:, :2]
    x_test_img = x_test.iloc[:, :2]

    x_train_gps = x_train.iloc[:, 2:]
    x_test_gps = x_test.iloc[:, 2:]

    #converts string values to integers
    y_train = [int(numeric_string) for numeric_string in y_train]
    y_test = [int(numeric_string) for numeric_string in y_test]

    print("-- Finished splitting")
    print("-- Loading images")

    now = datetime.now()
    current_time = now.strftime("%H:%M:%S")
    print("-- Starting Training @ ", current_time)

    #creates tensorboard for data visualisation
    tensorboard = TensorBoard(log_dir="logs\\final-{}".format(time()))

    #prepares generator functions
    trainGen = doTrain(dataframe=x_train_img, gps=x_train_gps, labels=y_train, batch_size=batch_size)
    testGen = doTrain(dataframe=x_test_img, gps=x_test_gps, labels=y_test, batch_size=batch_size)

    #gets step size
    trainSteps = int(len(y_train) / batch_size)
    testSteps = int(len(y_test) / batch_size)

    #trains the model
    model.fit(
        trainGen,
        steps_per_epoch=trainSteps,
        epochs=noEpochs,
        callbacks=[tensorboard],
        validation_data=testGen,
        validation_steps=testSteps)

    return model

#training generator function - yields the gps and image batches for input into to the training function
def doTrain(dataframe, gps, labels, batch_size=32):

    # suffled indices    
    #idx = np.random.permutation( X.shape[0])

    #correct values
    y_labs = np.reshape(labels, (len(gps.values), 1))
    x_gps = gps.astype(np.float).values

    #prepares augmentation model
    datagen = ImageDataGenerator(
            rescale = 1./255,
            rotation_range=20,
            width_shift_range=0.15,
            height_shift_range=0.15,
            horizontal_flip=False,
            zoom_range=0.5,
            shear_range = 0.2,
            )

    #loads images from dataframe
    batches = datagen.flow_from_dataframe(
            dataframe=dataframe,
            x_col="url", 
            y_col="landmarkID", 
            target_size=(224, 224), 
            batch_size=batch_size,
            shuffle=False,
            class_mode="sparse")


    i = 0

    while i == 0:
        i = 0

        idx0 = 0

        #loops through batches
        for batch in batches:
            #gets indexes
            idx1 = idx0 + batch[0].shape[0]

            #outputs specific data
            yield [x_gps[idx0:idx1], batch[0]], batch[1] # Images and GPS
            #yield batch[0], batch[1] # Just Images

            idx0 = idx1
            if(idx0 >= len(gps.values)):
                break
