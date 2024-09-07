
# Image handler worker

Simple Google Cloud Run function triggered by a pub/sub messages and process images, either doing image resizing or adding watermark. It follows a request / reply pattern using an output topic to inform to the original publisher the result of the operation.

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

- APP_JOBTYPE: job operation mode -> resize | watermark


- APP_BUCKET_ENDPOINT: real bucket location or local minio url.
- APP_BUCKET_KEY: bucket key (HMAC key for gcloud env.) or local minio key
- APP_BUCKET_SECRET: bucket service (HMAC secret	 for gcloud env.) or local minio secret.
- APP_BUCKET_NAME: bucket name (HMAC key for gcloud env.) or local minio key
- APP_BUCKET_INPUT: the input directory in the bucket from which images are gotten from
- APP_BUCKET_OUTPUT: the output directory in the bucket from which resulting images are put to.


- APP_PUBSUB_PROJECTID: project where the pub/sub topic for responses is located. 
- APP_PUBSUB_TOPICID: the topic id for responses.

- PUBSUB_EMULATOR_HOST=[::1]:8432 //only for local pubsub emulator testing
- PUBSUB_PROJECT_ID=my-project-id //only for local pubsub emulator testing
## Local

## Run locally

- gcloud beta emulators pubsub start --project=test-project [options]
- $(gcloud beta emulators pubsub env-init)
- export variables from terminal

````
export APP_JOBTYPE=resize
export APP_BUCKET_ENDPOINT=http://localhost:9000
export APP_BUCKET_KEY=youraccesskey 
export APP_BUCKET_SECRET=yoursecretkey 
export APP_BUCKET_NAME=evendriven-poc 
export APP_BUCKET_INPUT=input/ 
export APP_BUCKET_OUTPUT=output/
export APP_PUBSUB_PROJECTID=test-project 
export APP_PUBSUB_TOPICID=image-handling-response-topic
 ```
    
- mvn function:run

- execute sub/pub emulated event

````
curl localhost:8080 \
  -X POST \
  -H "Content-Type: application/json" \
  -H "ce-id: 123451234512345" \
  -H "ce-specversion: 1.0" \
  -H "ce-time: 2020-01-02T12:34:56.789Z" \
  -H "ce-type: google.cloud.pubsub.topic.v1.messagePublished" \
  -H "ce-source: //pubsub.googleapis.com/projects/MY-PROJECT/topics/MY-TOPIC" \
  -d '{
        "message": {
          "data": "ewogICAgImpvYlVVSUQiOiAiMTIzNDU2IiwKICAgICJmaWxlbmFtZSI6ICJ2MTAwMy03MWMuanBnIiwKICAgICJyZXNvbHV0aW9uIjogewogICAgICAgICJ3aWR0aCI6IDgwMCwKICAgICAgICAiaGVpZ2h0IjogNjAwCiAgICB9Cn0=",
          "attributes": {
             "attr1":"attr1-value"
          }
        },
        "subscription": "projects/MY-PROJECT/subscriptions/MY-SUB"
      }' 
 ```

