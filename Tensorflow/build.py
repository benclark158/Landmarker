from __future__ import absolute_import, division, print_function, unicode_literals

import tensorflow as tf

import time

from tensorflow.keras import datasets, layers, models, Model
from tensorflow.keras.layers import Layer
from keras.models import Sequential

#depthwise layer made up of other sub-layers
class dwLayer(Layer):

  #initialises the sublayers
  def __init__(self, lname, strides):
    super(dwLayer, self).__init__(name=lname)

    self.lname = lname
    self.strides = strides

    self.dw2d = tf.keras.layers.DepthwiseConv2D(
        kernel_size=(3,3),
        strides=strides,
        data_format='channels_last',
        padding='same')
    
    self.bna = tf.keras.layers.BatchNormalization()

  #creates the layer
  def call(self, input_tensor, training=False):
    x = self.dw2d(input_tensor)
    x = self.bna(x, training=training)

    return tf.nn.relu(x)

  #allows saving
  def get_config(self):
    base_config = super(dwLayer, self).get_config()
    config = {'lname': self.lname,
              'strides': self.strides}

    return dict(list(base_config.items()) + list(config.items()))

#point wise layer - made up of other layers
class pwLayer(Layer):

  #initialises the layers within this layer
  def __init__(self, lname, filters):
    super(pwLayer, self).__init__(name=lname)

    self.filters = filters
    self.lname = lname

    self.conv2d = tf.keras.layers.Conv2D(
        filters=filters,
        kernel_size=(1,1),
        strides=(1,1),
        data_format='channels_last',
        padding='same')
    
    self.bnb = tf.keras.layers.BatchNormalization()

  #creates this layer
  def call(self, input_tensor, training=False):

    x = self.conv2d(input_tensor)
    x = self.bnb(x, training=training)

    return tf.nn.relu(x)

  #allows the model to save
  def get_config(self):
    base_config = super(pwLayer, self).get_config()
    config = {'lname': self.lname,
              'filters': self.filters}

    return dict(list(base_config.items()) + list(config.items()))

	
#build the model
def makeModel(numClasses):
  print("building\n")
  time.sleep(0.01)

  modelImg = models.Sequential()
  modelImg.add(layers.InputLayer(input_shape=(224, 224, 3)))

  #3x3 Conv
  modelImg.add(layers.Conv2D(name='conv', filters=32, kernel_size=(3, 3), activation='relu', strides=(2, 2), data_format='channels_last', padding='same'))

  modelImg.add(dwLayer(lname='dw-1', strides=(1,1)))
  modelImg.add(pwLayer(lname='pw-1', filters=64))

  modelImg.add(dwLayer(lname='dw-2', strides=(2,2)))
  modelImg.add(pwLayer(lname='pw-2', filters=128))

  modelImg.add(dwLayer(lname='dw-3', strides=(1,1)))
  modelImg.add(pwLayer(lname='pw-3', filters=128))

  modelImg.add(dwLayer(lname='dw-4', strides=(2,2)))
  modelImg.add(pwLayer(lname='pw-4', filters=256))

  modelImg.add(dwLayer(lname='dw-5', strides=(1,1)))
  modelImg.add(pwLayer(lname='pw-5', filters=256))

  modelImg.add(dwLayer(lname='dw-6', strides=(2,2)))
  modelImg.add(pwLayer(lname='pw-6', filters=512))

  for x in range(0, 4): 
    modelImg.add(dwLayer(lname='dw-' + str(7+x), strides=(1,1)))
    modelImg.add(pwLayer(lname='pw-' + str(7+x), filters=512))

  #modelImg.add(layers.Dense(0.2))
  modelImg.add(dwLayer(lname='dw-12', strides=(2,2)))
  modelImg.add(pwLayer(lname='pw-12', filters=1024))

  modelImg.add(dwLayer(lname='dw-13', strides=(1,1)))
  modelImg.add(pwLayer(lname='pw-13', filters=1024))

  modelImg.add(layers.AveragePooling2D((7,7)))

  modelImg.add(layers.Flatten())

  #gps fully connected layers
  inputGPS = models.Sequential()
  inputGPS.add(layers.InputLayer(input_shape=(2,)))
  inputGPS.add(layers.Dense(4, activation='relu'))
  inputGPS.add(layers.Dense(4, activation='relu'))
  inputGPS.add(layers.Dense(4, activation='relu'))

  combinedModel = layers.concatenate([inputGPS.output, modelImg.output])

  x = (layers.Dense(512, activation='relu'))(combinedModel)
  x = (layers.Dense(128, activation='relu'))(x)
  x = (layers.Dense(128, activation='relu'))(x)
  x = (layers.Dense(64, activation='relu'))(x)
  x = (layers.Dense(numClasses, activation='softmax'))(x)

  
  model = tf.keras.Model(inputs=[inputGPS.input, modelImg.input], outputs=x)
  
  #no gps fully connected layers
  #modelImg.add(layers.Dense(512, activation='relu'))
  #modelImg.add(layers.Dense(128, activation='relu'))
  #modelImg.add(layers.Dense(128, activation='relu'))
  #modelImg.add(layers.Dense(64, activation='relu'))
  #modelImg.add(layers.Dense(numClasses, activation='softmax'))
  #model = modelImg

  #opt = tf.keras.optimizers.SGD(lr=0.001)
  opt = tf.keras.optimizers.Adam(lr=0.001)#1e-6)

  model.compile(optimizer='adam',
		loss='sparse_categorical_crossentropy',
		metrics=['accuracy'])

  model.summary()

  return model
