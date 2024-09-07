package com.poc.reactive.demo.services.impl;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import javax.imageio.ImageIO;

import com.poc.reactive.demo.app.contract.ImageProcessingInputResolution;
import com.poc.reactive.demo.exceptions.ImageHandlingException;
import com.poc.reactive.demo.services.ITransformationJob;

import net.coobird.thumbnailator.Thumbnails;

public class TransformationResize implements ITransformationJob {

	@Override
	public byte[] transform(byte[] sourceFile, ImageProcessingInputResolution configuration)
			throws ImageHandlingException {
		System.out.println("imageResizer start");

		try (InputStream stream = new ByteArrayInputStream(sourceFile);
				ByteArrayOutputStream baos = new ByteArrayOutputStream();) {
			List<BufferedImage> output = Thumbnails.of(stream).size(configuration.width(), configuration.height())
					.asBufferedImages();

			// Write the BufferedImage to the ByteArrayOutputStream
			ImageIO.write(output.get(0), "jpeg", baos);

			// Convert ByteArrayOutputStream to byte[]
			return baos.toByteArray();

		} catch (IOException e) {
			throw new ImageHandlingException("unable to add watermark to image: " + e.getMessage(), e);
		}
	}

}
