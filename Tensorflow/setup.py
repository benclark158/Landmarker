from __future__ import absolute_import, division, print_function, unicode_literals

import tensorflow as tf

import time
import build, training
import webbrowser as wb
import traceback
import urllib.parse
import cv2 as cv
import numpy as np

from tensorflow.keras import datasets, layers, models
import tensorflowjs as tfjs

#does everything for the network
def makeTheNetwork():

  #determines which opertations to cary out
  doBuild = True
  doTrain = True
  doSave = True

  model = []

  #number of classes in this model
  numClasses = 84

  if(doBuild):
    time.sleep(1)
    print("Making model")
    time.sleep(1)

    #builds the model
    model = build.makeModel(numClasses)

  if(doTrain):

    #Does training of the network

    time.sleep(1)
    print("Training model")
    time.sleep(1)

    model = training.training(model=model, numClasses=numClasses, noEpochs=30)

    time.sleep(1)
    print("Finished training")
    time.sleep(1)

  if(doSave):
    time.sleep(1)
    print("Saving model")
    time.sleep(1)

    #save as js model
    #tfjs.converters.save_keras_model(model, "E:\\")

    #save as .pb model
    model.save('saved_model\imgGPS') 

    #Save as tflite model
    converter = tf.lite.TFLiteConverter.from_keras_model(model_trained)
    converter.optimizations = [tf.lite.Optimize.DEFAULT]
    tflite_quant_model = converter.convert()
    open("converted_model.tflite", "wb").write(tflite_quant_model)

  time.sleep(1)
  print("====================")
  print("     FINSIHED")
  print("====================")


makeTheNetwork()