/*
 * LinShare is an open source filesharing software, part of the LinPKI software
 * suite, developed by Linagora.
 * 
 * Copyright (C) 2017-2018 LINAGORA
 * 
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Affero General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option) any
 * later version, provided you comply with the Additional Terms applicable for
 * LinShare software by Linagora pursuant to Section 7 of the GNU Affero General
 * Public License, subsections (b), (c), and (e), pursuant to which you must
 * notably (i) retain the display of the “LinShare™” trademark/logo at the top
 * of the interface window, the display of the “You are using the Open Source
 * and free version of LinShare™, powered by Linagora © 2009–2018. Contribute to
 * Linshare R&D by subscribing to an Enterprise offer!” infobox and in the
 * e-mails sent with the Program, (ii) retain all hypertext links between
 * LinShare and linshare.org, between linagora.com and Linagora, and (iii)
 * refrain from infringing Linagora intellectual property rights over its
 * trademarks and commercial brands. Other Additional Terms apply, see
 * <http://www.linagora.com/licenses/> for more details.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU Affero General Public License and
 * its applicable Additional Terms for LinShare along with this program. If not,
 * see <http://www.gnu.org/licenses/> for the GNU Affero General Public License
 * version 3 and <http://www.linagora.com/licenses/> for the Additional Terms
 * applicable to LinShare software.
 */
package org.linagora.linshare.core.facade.webservice.admin.impl;

import java.util.List;

import org.apache.commons.lang.Validate;
import org.linagora.linshare.core.domain.constants.Role;
import org.linagora.linshare.core.domain.constants.UpgradeTaskType;
import org.linagora.linshare.core.domain.entities.UpgradeTask;
import org.linagora.linshare.core.domain.entities.User;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.facade.webservice.admin.UpgradeTaskFacade;
import org.linagora.linshare.core.facade.webservice.admin.dto.UpgradeTaskDto;
import org.linagora.linshare.core.service.AccountService;
import org.linagora.linshare.core.service.UpgradeTaskService;

import com.google.common.base.Function;
import com.google.common.collect.Lists;

public class UpgradeTaskFacadeImpl extends AdminGenericFacadeImpl implements UpgradeTaskFacade {

	protected UpgradeTaskService service;

	public UpgradeTaskFacadeImpl(AccountService accountService, UpgradeTaskService service) {
		super(accountService);
		this.service = service;
	}

	@Override
	public UpgradeTaskDto find(UpgradeTaskType identifier) throws BusinessException {
		Validate.notNull(identifier, "Missing upgrade task uuid");
		User authUser = checkAuthentication(Role.SUPERADMIN);
		UpgradeTask task = service.find(authUser, identifier);
		return new UpgradeTaskDto(task);
	}

	@Override
	public List<UpgradeTaskDto> findAll() throws BusinessException {
		User authUser = checkAuthentication(Role.ADMIN);
		List<UpgradeTask> all = service.findAll(authUser);
		Function<UpgradeTask, UpgradeTaskDto> convert = null;
		if (authUser.hasSuperAdminRole()) {
			convert = new Function<UpgradeTask, UpgradeTaskDto>() {
				@Override
				public UpgradeTaskDto apply(UpgradeTask obj) {
					return new UpgradeTaskDto(obj);
				}
			};
		} else {
			convert = new Function<UpgradeTask, UpgradeTaskDto>() {
				@Override
				public UpgradeTaskDto apply(UpgradeTask obj) {
					UpgradeTaskDto dto = new UpgradeTaskDto();
					dto.setStatus(obj.getStatus());
					return dto;
				}
			};
		}
		return Lists.transform(all, convert);
	}

	@Override
	public UpgradeTaskDto update(UpgradeTaskDto upgradeTaskDto) throws BusinessException {
		Validate.notNull(upgradeTaskDto, "Missing upgrade task");
		User authUser = checkAuthentication(Role.SUPERADMIN);
		UpgradeTask task = service.find(authUser, upgradeTaskDto.getIdentifier());
		task.setAsyncTaskUuid(upgradeTaskDto.getAsyncTaskUuid());
		task.setStatus(upgradeTaskDto.getStatus());
		return new UpgradeTaskDto(service.update(authUser, task));
	}

}
