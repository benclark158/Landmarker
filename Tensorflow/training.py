import glob
import tensorflow as tf
from keras.preprocessing.image import ImageDataGenerator
from keras.utils import Sequence
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

from datetime import datetime

import progressbar

def createGenerator( dataframe, gps_lat, gps_long, labels):

    while True:
        # suffled indices    
        #idx = np.random.permutation( X.shape[0])
        # create image generator
        datagen = ImageDataGenerator(
            rescale=1./255,
            rotation_range=10, #180,  # randomly rotate images in the range (degrees, 0 to 180)
            width_shift_range=0.1, #0.1,  # randomly shift images horizontally (fraction of total width)
            height_shift_range=0.1, #0.1,  # randomly shift images vertically (fraction of total height)
            horizontal_flip=True,  # randomly flip images
            vertical_flip=False)  # randomly flip images

        batches = datagen.flow_from_dataframe(dataframe=dataframe, x_col="url", y_col="landmarkID", target_size=(224, 224), batch_size=1, shuffle=False)
        idx0 = 0
        for batch in batches:
            #idx1 = idx0 + batch[0].shape[0]

            yield [[gps_lat[idx0], gps_long[idx0]], batch[0]], batch[1]

            #print(batch[1])
            idx0 = idx0 + 1
            #idx0 = idx1
            if idx0 >= len(dataframe.values):
                break


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


def training(model, noEpochs, numClasses, index):
    attList = ["landmarkID", "url", "noise_lat", "noise_long"]
    attDF = pd.read_csv('C:\\Users\\Ben Clark\\Desktop\\partitionedDataset\\partition_' + str(index) + '.csv', usecols=attList, dtype=str)

    img_rows, img_cols = 244, 244

    batch_size = 32
    #input_shape = (img_rows, img_cols, 3)

    #print(train)

    labels = attDF['landmarkID'].values
    #labels = keras.utils.to_categorical(labels, numClasses)

    #x_train, x_test, y_train, y_test = train_test_split(attDF, labels, test_size = 0.3, random_state=666)

    #x is inputs 
    # y is labels

    x_train_img = attDF['url'].values#x_train.iloc[:, 1]
    #x_test_img = x_test.iloc[:, :2]

    x_train_gps_lat = attDF['noise_lat'].values
    x_train_gps_long = attDF['noise_long'].values
    #x_test_gps = x_test.iloc[:,2:]

    print("-- Finished splitting")
    print("-- Loading images")

    #train_images = loadImages(x_train_img.values, len(x_train_img.values))
    #test_images = loadImages(x_test_img.values, len(x_test_img.values))

    #steps = len(x_train_img.values) + len(x_test_img.values) / batch_size

    now = datetime.now()

    current_time = now.strftime("%H:%M:%S")
    print("Current Time =", current_time)

    print("-- Starting Training")

    #idg = MultiGenerator(gps=x_train_gps, imgs=imgDF, labels=labels)

    tensorboard = TensorBoard(log_dir="logs\{}".format(time()))

    model.fit(
        createGenerator(dataframe=attDF, gps_lat=x_train_gps_lat, gps_long=x_train_gps_lat, labels=labels),
        batch_size=None,
        epochs=noEpochs,
        callbacks=[tensorboard],
        verbose=1,
        #validation_data=([x_test_gps, test_images], y_test)
        )

    del imgDF
    del attDF
    del labels

    del x_train
    del x_test
    del y_train
    del y_test

    del x_train_img
    del x_test_img

    del x_train_gps
    del x_test_gps

    #del train_images
    #del test_images

    return model


