from __future__ import absolute_import, division, print_function, unicode_literals

import tensorflow as tf

import time

from tensorflow.keras import datasets, layers, models, Model
from keras.models import Sequential

class dwLayer(tf.keras.Model):
  def __init__(self, name, strides):
    super(dwLayer, self).__init__(name=name)

    self.dw2d = tf.keras.layers.DepthwiseConv2D(
        kernel_size=(3,3),
        strides=strides,
        data_format='channels_last',
        padding='same')
    
    self.bna = tf.keras.layers.BatchNormalization()

  def call(self, input_tensor, training=False):
    x = self.dw2d(input_tensor)
    x = self.bna(x, training=training)

    #x += input_tensor

    return tf.nn.relu(x)

class pwLayer(tf.keras.Model):
  def __init__(self, name, filters):
    super(pwLayer, self).__init__(name=name)

    self.conv2d = tf.keras.layers.Conv2D(
        filters=filters,
        kernel_size=(1,1),
        strides=(1,1),
        data_format='channels_last',
        padding='same')
    
    self.bnb = tf.keras.layers.BatchNormalization()

  def call(self, input_tensor, training=False):

    x = self.conv2d(input_tensor)
    x = self.bnb(x, training=training)

    #x += input_tensor

    return tf.nn.relu(x)

	
def makeModel(numClasses):
  print("building\n")
  time.sleep(0.01)

  modelImg = models.Sequential()
  modelImg.add(layers.InputLayer(input_shape=(224, 224, 3)))

  #3x3 Conv
  modelImg.add(layers.Conv2D(name='conv', filters=32, kernel_size=(3, 3), activation='relu', strides=(2, 2), data_format='channels_last', padding='same'))

  modelImg.add(dwLayer(name='dw-1', strides=(1,1)))
  modelImg.add(pwLayer(name='pw-1', filters=64))

  modelImg.add(dwLayer(name='dw-2', strides=(2,2)))
  modelImg.add(pwLayer(name='pw-2', filters=128))

  modelImg.add(dwLayer(name='dw-3', strides=(1,1)))
  modelImg.add(pwLayer(name='pw-3', filters=128))

  modelImg.add(dwLayer(name='dw-4', strides=(2,2)))
  modelImg.add(pwLayer(name='pw-4', filters=256))

  modelImg.add(dwLayer(name='dw-5', strides=(1,1)))
  modelImg.add(pwLayer(name='pw-5', filters=256))

  modelImg.add(dwLayer(name='dw-6', strides=(2,2)))
  modelImg.add(pwLayer(name='pw-6', filters=512))

  for x in range(0, 4): 
    modelImg.add(dwLayer(name='dw-' + str(7+x), strides=(1,1)))
    modelImg.add(pwLayer(name='pw-' + str(7+x), filters=512))

  modelImg.add(dwLayer(name='dw-12', strides=(2,2)))
  modelImg.add(pwLayer(name='pw-12', filters=1024))

  modelImg.add(dwLayer(name='dw-13', strides=(1,1)))
  modelImg.add(pwLayer(name='pw-13', filters=1024))

  modelImg.add(layers.AveragePooling2D((7,7)))

  modelImg.add(layers.Flatten())

  inputGPS = models.Sequential()
  inputGPS.add(layers.InputLayer(input_shape=(2,)))

  combinedModel = layers.concatenate([inputGPS.output, modelImg.output])

  x = (layers.Dense(64, activation='relu'))(combinedModel)
  x = (layers.Dense(64, activation='relu'))(x)
  x = (layers.Dense(64, activation='relu'))(x)
  x = (layers.Dense(numClasses, activation='softmax'))(x)

  
  model = tf.keras.Model(inputs=[inputGPS.input, modelImg.input], outputs=x)
  
  model.summary()

  model.compile(optimizer='adam',
		loss='sparse_categorical_crossentropy',
		metrics=['accuracy'])

  return model
