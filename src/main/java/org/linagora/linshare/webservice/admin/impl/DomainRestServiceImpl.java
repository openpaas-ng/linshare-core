/*
 * LinShare is an open source filesharing software, part of the LinPKI software
 * suite, developed by Linagora.
 * 
 * Copyright (C) 2014 LINAGORA
 * 
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Affero General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option) any
 * later version, provided you comply with the Additional Terms applicable for
 * LinShare software by Linagora pursuant to Section 7 of the GNU Affero General
 * Public License, subsections (b), (c), and (e), pursuant to which you must
 * notably (i) retain the display of the “LinShare™” trademark/logo at the top
 * of the interface window, the display of the “You are using the Open Source
 * and free version of LinShare™, powered by Linagora © 2009–2014. Contribute to
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
package org.linagora.linshare.webservice.admin.impl;

import java.util.Set;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.facade.webservice.admin.DomainFacade;
import org.linagora.linshare.core.facade.webservice.admin.FunctionalityFacade;
import org.linagora.linshare.webservice.WebserviceBase;
import org.linagora.linshare.webservice.admin.DomainRestService;
import org.linagora.linshare.webservice.dto.DomainDto;
import org.linagora.linshare.webservice.dto.FunctionalityDto;

import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;
import com.wordnik.swagger.annotations.ApiResponse;
import com.wordnik.swagger.annotations.ApiResponses;

@Path("/domains")
@Api(value = "/rest/admin/domains", description = "Domains service.")
@Produces({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
@Consumes({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
public class DomainRestServiceImpl extends WebserviceBase implements
		DomainRestService {

	private final DomainFacade domainFacade;

	private final FunctionalityFacade functionalityFacade;

	public DomainRestServiceImpl(final DomainFacade domainFacade,
			final FunctionalityFacade functionalityFacade) {
		this.domainFacade = domainFacade;
		this.functionalityFacade = functionalityFacade;
	}

	@Path("/")
	@GET
	@ApiOperation(value = "Find all domains.", response = DomainDto.class, responseContainer = "Set")
	@ApiResponses({ @ApiResponse(code = 403, message = "User isn't a super admin.") })
	@Override
	public Set<DomainDto> findAll() throws BusinessException {
		return domainFacade.findAll();
	}

	@Path("/{id}")
	@GET
	@ApiOperation(value = "Find a domain.", response = DomainDto.class)
	@ApiResponses({ @ApiResponse(code = 403, message = "User isn't a super admin.") })
	@Override
	public DomainDto find(@PathParam(value = "id") String domainId,
			@QueryParam("tree") @DefaultValue("false") boolean tree)
			throws BusinessException {
		return domainFacade.find(domainId, tree);
	}

	@Path("/")
	@POST
	@ApiOperation(value = "Create a domain.")
	@ApiResponses({ @ApiResponse(code = 403, message = "User isn't a super admin.") })
	@Override
	public void create(DomainDto domain) throws BusinessException {
		domainFacade.create(domain);
	}

	@Path("/")
	@PUT
	@ApiOperation(value = "Update a domain.")
	@ApiResponses({ @ApiResponse(code = 403, message = "User isn't a super admin.") })
	@Override
	public void update(DomainDto domain) throws BusinessException {
		domainFacade.update(domain);
	}

	@Path("/")
	@DELETE
	@ApiOperation(value = "Delete a domain.")
	@ApiResponses({ @ApiResponse(code = 403, message = "User isn't a super admin.") })
	@Override
	public void delete(DomainDto domain) throws BusinessException {
		domainFacade.delete(domain);
	}

	/*
	 * Functionalities
	 */

	@Path("/{domainId}/functionalities")
	@GET
	@ApiOperation(value = "Find all domain's functionalities.")
	@Override
	public Set<FunctionalityDto> findAllFunctionalities(
			@PathParam(value = "domainId") String domainId)
			throws BusinessException {
		return functionalityFacade.findAll(domainId);
	}

	@Path("/{domainId}/functionalities/{funcId}")
	@GET
	@ApiOperation(value = "Find a domain's functionality.")
	@Override
	public FunctionalityDto findFunctionality(
			@PathParam(value = "domainId") String domainId,
			@PathParam(value = "funcId") String funcId)
			throws BusinessException {
		return functionalityFacade.get(domainId, funcId);
	}

	@Path("/{domainId}/functionalities")
	@PUT
	@ApiOperation(value = "Update a domain's functionality.")
	@Override
	public void updateFunctionality(
			@PathParam(value = "domainId") String domainId, FunctionalityDto func)
			throws BusinessException {
		functionalityFacade.update(domainId, func);
	}

	@Path("/{domainId}/functionalities")
	@DELETE
	@ApiOperation(value = "Delete a domain's functionality.")
	@Override
	public void deleteFunctionality(
			@PathParam(value = "domainId") String domainId, FunctionalityDto func)
			throws BusinessException {
		functionalityFacade.delete(domainId, func);
	}
}
