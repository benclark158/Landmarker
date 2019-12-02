from __future__ import absolute_import, division, print_function, unicode_literals

import tensorflow as tf
import tensorflow_datasets as tfds

import time
import build, training

from tensorflow.keras import datasets, layers, models

if(1==0):
  time.sleep(1)
  print("Making model")
  time.sleep(1)

  model = build.makeModel()

  time.sleep(1)
  print("Loading Training Data")
  time.sleep(1)

if(1==0):
  training.training()

  time.sleep(1)
  print("Training")
  time.sleep(1)

#model.fit(images, labels, epochs=5)
#model.evaluate(images, labels)


tf.compat.v1.enable_eager_execution()

# See available datasets
print(tfds.list_builders())

# Construct a tf.data.Dataset
ds_train = tfds.load(name="mnist", split="train", shuffle_files=True)

print("\n\n\n\n test \n\n\n\n\n")

# Build your input pipeline
ds_train = ds_train.shuffle(1000).batch(128).prefetch(10)
for features in ds_train.take(1):
  image, label = features["image"], features["label"]
