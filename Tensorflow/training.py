import glob
import tensorflow as tf
import tensorflow_datasets as tfds

def training():
    tf.compat.v1.enable_eager_execution()
    
    rps_train = tfds.load(name="rock_paper_scissors", split="train")
    #rps_test = tfds.load(name="rock_paper_scissors", split="test")
    #return (images, labels)

    # The following is the equivalent of the `load` call above.

    # You can fetch the DatasetBuilder class by string
    mnist_builder = tfds.builder('rock_paper_scissors')
    
    # Download the dataset
    mnist_builder.download_and_prepare()

    # Construct a tf.data.Dataset
    ds = mnist_builder.as_dataset(split='train')

    # Get the `DatasetInfo` object, which contains useful information about the
    # dataset and its features
    info = mnist_builder.info
    print(info)
    print(info.features['label'].names)

    ds_train = rps_train.shuffle(1000).batch(128).prefetch(10)

    for features in ds_train.take(1):
        image, label = features["image"], features["label"]
        print("1")

    
