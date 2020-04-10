from __future__ import absolute_import, division, print_function, unicode_literals

import tensorflow as tf

import time
import build, training

from tensorflow.keras import datasets, layers, models

if(1==1):
  time.sleep(1)
  print("Making model")
  time.sleep(1)

  model = build.makeModel()

  time.sleep(1)
  print("Training model")
  time.sleep(1)

if(1==1):
  model_trained = training.training(model=model, steps=10, noEpochs=1)

  time.sleep(1)
  print("Finished training")
  time.sleep(1)


model_trained.save('saved_model\my_model') 


converter = tf.lite.TFLiteConverter.from_keras_model(model_trained)
converter.optimizations = [tf.lite.Optimize.DEFAULT]
tflite_quant_model = converter.convert()
open("converted_model.tflite", "wb").write(tflite_quant_model)

#new_model = tf.keras.models.load_model('saved_model/my_model')


#model.fit(images, labels, epochs=5)
#model.evaluate(images, labels)


#tf.compat.v1.enable_eager_execution()

# See available datasets
#print(tfds.list_builders())

# Construct a tf.data.Dataset
#ds_train = tfds.load(name="mnist", split="train", shuffle_files=True)

#print("\n\n\n\n test \n\n\n\n\n")

# Build your input pipeline
#ds_train = ds_train.shuffle(1000).batch(128).prefetch(10)
#for features in ds_train.take(1):
  #image, label = features["image"], features["label"]
