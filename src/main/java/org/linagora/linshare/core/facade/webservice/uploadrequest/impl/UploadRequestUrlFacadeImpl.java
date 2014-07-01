package org.linagora.linshare.core.facade.webservice.uploadrequest.impl;

import java.util.Set;

import org.apache.commons.lang.Validate;
import org.linagora.linshare.core.domain.entities.Account;
import org.linagora.linshare.core.domain.entities.MimeType;
import org.linagora.linshare.core.domain.entities.UploadRequest;
import org.linagora.linshare.core.domain.entities.UploadRequestUrl;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.facade.webservice.uploadrequest.UploadRequestUrlFacade;
import org.linagora.linshare.core.facade.webservice.uploadrequest.dto.ContactDto;
import org.linagora.linshare.core.facade.webservice.uploadrequest.dto.MimeTypeDto;
import org.linagora.linshare.core.facade.webservice.uploadrequest.dto.UploadRequestDto;
import org.linagora.linshare.core.service.MimePolicyService;
import org.linagora.linshare.core.service.UploadRequestUrlService;

import com.google.common.collect.Sets;

public class UploadRequestUrlFacadeImpl implements UploadRequestUrlFacade {

	private final UploadRequestUrlService uploadRequestUrlService;
	
	private final MimePolicyService mimePolicyService;
	

	public UploadRequestUrlFacadeImpl(
			final UploadRequestUrlService uploadRequestUrlService,
			final MimePolicyService mimePolicyService) {
		this.uploadRequestUrlService = uploadRequestUrlService;
		this.mimePolicyService = mimePolicyService;
	}
	
	@Override
	public UploadRequestDto find(String uploadRequestUrlUuid) throws BusinessException {
		Validate.notEmpty(uploadRequestUrlUuid);
		UploadRequestUrl requestUrl = uploadRequestUrlService.find(uploadRequestUrlUuid);
		UploadRequestDto dto = transform(requestUrl);
		return dto;
	}

	@Override
	public UploadRequestDto close(UploadRequestDto req)
			throws BusinessException {
		Validate.notNull(req);
		Validate.notEmpty(req.getUuid());
		return transform(uploadRequestUrlService.close(req.getUuid()));
	}

	@Override
	public Set<MimeTypeDto> findAllMyMimeTypes(String uploadRequestUrlUuid) throws BusinessException {
		Set<MimeTypeDto> res = Sets.newHashSet();
		UploadRequestUrl requestUrl = uploadRequestUrlService.find(uploadRequestUrlUuid);
		Set<MimeType> mimeTypes = mimePolicyService.findAllMyMimeTypes(requestUrl.getUploadRequest().getOwner());
		for (MimeType mimeType : mimeTypes) {
			res.add(new MimeTypeDto(mimeType));
		}
		return res;
	}

	private UploadRequestDto transform(UploadRequestUrl requestUrl) throws BusinessException {
		UploadRequest request = requestUrl.getUploadRequest();
		UploadRequestDto dto = new UploadRequestDto(request);
		dto.setRecipient(new ContactDto(requestUrl.getContact()));
		dto.setProtectedByPassword(requestUrl.isProtectedByPassword());

		Account owner = requestUrl.getUploadRequest().getOwner();
		Set<MimeType> mimeTypes = mimePolicyService.findAllMyMimeTypes(owner);
		for (MimeType mimeType : mimeTypes) {
			String extension = mimeType.getExtensions();
			if (!extension.isEmpty()) {
				dto.addMimeTypes(extension.substring(1));
			}
		}
		return dto;
	}
}