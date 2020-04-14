from __future__ import absolute_import, division, print_function, unicode_literals

import tensorflow as tf

import time
import build, training

from tensorflow.keras import datasets, layers, models

doBuild = True
doTrain = True
doSave = False

numClasses = 15129+1

if(doBuild):
  time.sleep(1)
  print("Making model")
  time.sleep(1)

  model = build.makeModel(numClasses)

if(doTrain):

  time.sleep(1)
  print("Training model")
  time.sleep(1)

  model_trained = training.training(model=model, steps=10, noEpochs=1, numClasses=numClasses)

  time.sleep(1)
  print("Finished training")
  time.sleep(1)

if(doSave):
  converter = tf.lite.TFLiteConverter.from_keras_model(model_trained)
  converter.optimizations = [tf.lite.Optimize.DEFAULT]
  tflite_quant_model = converter.convert()
  open("converted_model.tflite", "wb").write(tflite_quant_model)

time.sleep(1)
print("====================")
print("     FINSIHED")
print("====================")