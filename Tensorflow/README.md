# Tensorflow

In this file, all of the Python code to generate and train the specific tensorflow models require for this project are included.

##MobileNet

This network is based of the Google MobileNet architecture, which can be found here > https://arxiv.org/pdf/1704.04861.pdf

## Uses

In order to build this model please follow the below instructions:

1. Prepare your data in accorance with the following scheme `"landmarkID", "url", "noise_lat", "noise_long"` and save as a CSV file
2. Edit the `setup.py` file. Change the `numClasses` variable to the number of classes that you have within your dataset
3. Change the `datasetLocation` variable to the file location of your CSV dataset
4. Save changes
5. Ensure that tensorflow, numpy, cv2 and tensorflowjs are installed
6. Run `python setup.py` from a comand line or terminal.

This will build, train and save the network using the data provided. The model will be saved in two formats `.pb` and `.tflite`. There is also the option to output as a tensorflowjs model.

**Note:** image url should be an absolute path to the image.
