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
package org.linagora.linshare.core.facade.webservice.admin.dto;

import javax.xml.bind.annotation.XmlRootElement;

import org.linagora.linshare.core.domain.entities.LdapUserProvider;

import com.wordnik.swagger.annotations.ApiModel;
import com.wordnik.swagger.annotations.ApiModelProperty;

@XmlRootElement(name = "LDAPUserProvider")
@ApiModel(value = "LDAPUserProvider", description = "Used to provide users from an LDAP directory")
public class LDAPUserProviderDto {

    @ApiModelProperty(value = "LdapConnectionId")
	private String ldapConnectionId = "";

    @ApiModelProperty(value = "DomainPatternId")
	private String domainPatternId = "";

    @ApiModelProperty(value = "BaseDn")
	private String baseDn = "";

	public LDAPUserProviderDto(final LdapUserProvider ldapUserProvider) {

		this.ldapConnectionId = ldapUserProvider.getLdapconnexion()
				.getIdentifier();
		this.domainPatternId = ldapUserProvider.getPattern().getIdentifier();
		this.baseDn = ldapUserProvider.getBaseDn();
	}

	public LDAPUserProviderDto() {
		super();
	}

	public String getLdapConnectionId() {
		return ldapConnectionId;
	}

	public void setLdapConnectionId(String ldapConnectionId) {
		this.ldapConnectionId = ldapConnectionId;
	}

	public String getDomainPatternId() {
		return domainPatternId;
	}

	public void setDomainPatternId(String domainPatternId) {
		this.domainPatternId = domainPatternId;
	}

	public String getBaseDn() {
		return baseDn;
	}

	public void setBaseDn(String baseDn) {
		this.baseDn = baseDn;
	}
}