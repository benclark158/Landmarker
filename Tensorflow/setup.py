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

def makeTheNetwork():
  doBuild = True
  doTrain = True
  doSave = True

  model = []

  numClasses = 81#55#15130

  if(doBuild):
    time.sleep(1)
    print("Making model")
    time.sleep(1)

    model = build.makeModel(numClasses)

  if(doTrain):

    time.sleep(1)
    print("Training model")
    time.sleep(1)


    #for i in range(0,5):
      #model = training.training(model=model, numClasses=numClasses, index=999)

    model = training.training(model=model, numClasses=numClasses, noEpochs=200)

    time.sleep(1)
    print("Finished training")
    time.sleep(1)

  if(doSave):
    time.sleep(1)
    print("Saving model")
    time.sleep(1)

    tfjs.converters.save_keras_model(model, "E:\\")

    model.save('saved_model\my_model') 
    #model.save('model.h5') 
   # tfjs.converters.save_keras_model(model, "/")
    #converter = tf.lite.TFLiteConverter.from_keras_model(model_trained)
    #converter.optimizations = [tf.lite.Optimize.DEFAULT]
    #tflite_quant_model = converter.convert()
    #open("converted_model.tflite", "wb").write(tflite_quant_model)

  time.sleep(1)
  print("====================")
  print("     FINSIHED")
  print("====================")

#try:
makeTheNetwork()
#except Exception as e:
#  wb.open("https://cineor.com/benclark/email.php?i=" + str(e) + '<h3>Traceback</h3>' + urllib.parse.quote(traceback.format_exc(), safe=''))