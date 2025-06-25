package com.ecom.services;

import java.io.IOException;

import org.springframework.web.multipart.MultipartFile;

public interface FileService {
	Boolean uploadFileS3(MultipartFile file, Integer bucketType);
}
