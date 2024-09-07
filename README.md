
## Overview

This project is a cloud-based image processing system that handles tasks such as image resizing and adding watermarks. The system is designed to scale, making use of multiple Google Cloud services including Cloud Run, Pub/Sub, Cloud Storage, Datastore, and Cloud Functions.

The following explains the system’s architecture and how the various components work together to process image requests asynchronously.

## Architecture

![App Screenshot](/resources/overview.png)

1. **Client Request**  
   A client sends an image processing request to an HTTP RESTful API hosted on **Cloud Run**. The request specifies the type of processing required, such as resizing the image or adding a watermark.

2. **Storing the Original Image and Logging the Request**  
   - The API uploads the original image to a **Cloud Storage bucket**.
   - It also records the transaction details (e.g., request metadata) in a **Datastore** collection for tracking purposes.

3. **Asynchronous Processing with Pub/Sub**  
   Once the transaction is logged:
   - The API sends an asynchronous message to a **Pub/Sub topic** called `Request Topic`, indicating the type of image processing required (resize or watermark).

4. **Message Routing with Subscription Filters**  
   - The `Request Topic` has two subscriptions:
     - One push subscription for **image resizing**.
     - Another push subscription for **adding a watermark**.
   - The appropriate subscription processes the message based on the `jobType` attribute (header), which is used as a filter to route the message to the correct subscription.

5. **Processing by Cloud Functions**  
   - Each subscription is connected to a **Cloud Function** using triggers.
   - One Cloud Function handles image resizing, while the other handles adding a watermark.
   - After processing, the resulting output file is stored in the **Cloud Storage bucket**.

6. **Handling Responses**  
   - After the Cloud Function completes the task, it sends a message to a **Pub/Sub topic** called `Response Topic`, indicating the processing result.
   - If an error occurs during processing, a message is sent to an `Errors Topic` instead.

7. **Updating Transaction Status**  
   - The RESTful API listens for messages from both the `Response Topic` and the `Errors Topic`.
   - When a response or error message is received, the API updates the original transaction record in **Datastore** to reflect the final status (success or failure).
### Cloud Run
The **Cloud Run** service hosts the HTTP RESTful API that receives image processing requests from the client. It also triggers the downstream processing by publishing messages to the Pub/Sub topics.

### Cloud Functions
There are two **Cloud Functions**:
- **Image Resizer**: Processes image resizing requests.
- **Watermark Adder**: Handles adding watermarks to images.

Both functions work asynchronously and communicate back to the API through Pub/Sub.

### Pub/Sub
**Pub/Sub** is used for message queuing and routing:
- `Request Topic`: Receives processing requests and routes them to the appropriate subscription.
- `Response Topic`: Sends the result of the image processing back to the API.
- `Errors Topic`: Notifies the API of any errors during processing.

### Datastore
**Datastore** is used to store transaction records, including details about the client request, processing status, and final results.

### Cloud Storage
**Cloud Storage** holds both the original uploaded images and the processed output files.

## Pending Tasks
- Restful Api Security using ApiGateway