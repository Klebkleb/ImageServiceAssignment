# ImageServiceAssignment
The assignment is implemented as requested. Instead of implementing AWS S3 Buckets I simply save images to the file system.
The 'src/resources/image' folder acts as the source location outside of AWS.
The 'aws_images' folder acts as the AWS S3 bucket.
If an error occurs loading images, please create these folders manually (although it should happen automatically).

## How to use it ##
Code should run out of the box.
Images for testing have to be put in the 'src/resources/image' folder BEFORE running.

Different predefined image types have to be added in json format in the 'src/resources/imagetype' folder, also before running. See other files for examples.

Query images as described in the assignment at the endpoint:
localhost:8080/image/show/{predefined-type-name}/{dummy-seo-name}/?reference={unique-file-name}

## Implemented features ##
- All resizing options except "source-name".
- Storing of original images
- Storing of optimized images
- Requested naming scheme
- Variable Jpeg compression

## Not implemented features ##
- Amazon AWS (Too much hassle for 6 hours :P, used file system instead)
- Error logging (Only basic console logs in place)
- Flushing images
- Environments

## Structure ##
- ResizeController: Contains the endpoint, calls the services, serves images.
- ImageProviderService: Emulates AWS. Gets images from resources. Saves and loads images from disk.
- ResizeService: Handles the logic for resizing images. Loads predefined image types from disk.
- PredefinedImageType: Contains the model for the predefined image types. Loaded from json in the ResizeRervice.
