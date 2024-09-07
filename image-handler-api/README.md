
# Image Api Loader

Rest api deploy as a Cloud Function. It handles UI http requests, queue image processing request in a pub/sub topic, and finally read responses using a pull subscription in a response topic.


# Dependencies
The following dependencies are required to run this project:

- Pub/Sub topic
- Cloud Storage Bucket

for local development they can be replaced with:

- pubsub-emulator: https://cloud.google.com/pubsub/docs/emulator

````
gcloud components install pubsub-emulator
gcloud components update

gcloud beta emulators pubsub start --project=PUBSUB_PROJECT_ID [options]
$(gcloud beta emulators pubsub env-init)
```


- minio: https://hub.docker.com/r/minio/minio

````
docker run -p 9000:9000 -p 9001:9001 \
    --name minio \
    -e "MINIO_ROOT_USER=youraccesskey" \
    -e "MINIO_ROOT_PASSWORD=yoursecretkey" \
    -v /path/to/data:/data \
    -v /path/to/config:/root/.minio \
    minio/minio server /data --console-address ":9001"
```

## Environment variables
The following variables are required to run this service:

- APP_BUCKET_ENDPOINT: real bucket location or local minio url.
- APP_BUCKET_KEY: bucket key (HMAC key for gcloud env.) or local minio key
- APP_BUCKET_SECRET: bucket service (HMAC secret	 for gcloud env.) or local minio secret.
- APP_BUCKET_NAME: bucket name (HMAC key for gcloud env.) or local minio key
- APP_BUCKET_INPUT: the input directory in the bucket from which images are gotten from
- APP_BUCKET_OUTPUT: the output directory in the bucket from which resulting images are put to.

- APP_PUBSUB_ERRORS_SUBSCRIPTION: topic for error messages.
- APP_PUBSUB_REQUEST_TOPIC: topic for job image requests
- APP_PUBSUB_RESPONSE_SUBSCRIPTION: topic for job image response

- PUBSUB_EMULATOR_HOST=[::1]:8432 //only for local pubsub emulator testing

## Run locally

- gcloud beta emulators pubsub start --project=test-project [options]
- $(gcloud beta emulators pubsub env-init)
- export variables from terminal

````
-- start pub/sub emulator

gcloud beta emulators pubsub start --project=PUBSUB_PROJECT_ID [options]
$(gcloud beta emulators pubsub env-init)

-- start firestore emulator
gcloud beta emulators firestore start


-- set environment variables

export APP_FIRESTORE_EMULATOR="127.0.0.1:8080"
export APP_BUCKET_ENDPOINT=http://localhost:9000
export APP_BUCKET_KEY=youraccesskey 
export APP_BUCKET_SECRET=yoursecretkey 
export APP_BUCKET_NAME=evendriven-poc 
export APP_BUCKET_INPUT=input/ 
export APP_BUCKET_OUTPUT=output/
export APP_PUBSUB_PROJECTID=test-project 
export APP_PUBSUB_TOPICID=image-handling-response-topic
 ```
    




image-handling-response-subscription
