/*
 * LinShare is an open source filesharing software, part of the LinPKI software
 * suite, developed by Linagora.
 * 
 * Copyright (C) 2013 LINAGORA
 * 
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Affero General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option) any
 * later version, provided you comply with the Additional Terms applicable for
 * LinShare software by Linagora pursuant to Section 7 of the GNU Affero General
 * Public License, subsections (b), (c), and (e), pursuant to which you must
 * notably (i) retain the display of the “LinShare™” trademark/logo at the top
 * of the interface window, the display of the “You are using the Open Source
 * and free version of LinShare™, powered by Linagora © 2009–2013. Contribute to
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
package org.linagora.linshare.auth.sso;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.linagora.linshare.auth.dao.LdapUserDetailsProvider;
import org.linagora.linshare.core.domain.entities.User;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.facade.AccountFacade;
import org.linagora.linshare.core.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.web.authentication.preauth.RequestHeaderAuthenticationFilter;
import org.springframework.util.Assert;

/**
 * This Spring Security filter is designed to filter authentication against a
 * LemonLDAP::NG Web Single Sign On
 * 
 * @author Clement Oudot &lt;coudot@linagora.com&gt;
 */
public class PreAuthenticationHeader extends RequestHeaderAuthenticationFilter {

	private UserRepository<User> userRepository;

	private AccountFacade accountFacade;

	private LdapUserDetailsProvider ldapUserDetailsProvider;

	private String principalRequestHeader;

	private String domainRequestHeader;

	/** List of IP / DNS hostname */
	private List<String> authorizedAddresses;

	private static Logger logger = LoggerFactory
			.getLogger(PreAuthenticationHeader.class);

	public PreAuthenticationHeader(String authorizedAddressesList) {
		super();
		if (authorizedAddressesList != null) {
			List<String> asList = Arrays.asList(authorizedAddressesList
					.split(","));
			this.authorizedAddresses = asList;
		} else {
			this.authorizedAddresses = new ArrayList<String>();
		}
	}

	@Override
	protected Object getPreAuthenticatedPrincipal(HttpServletRequest request) {
		// Do not throw exception if header is not set
		String authenticationHeader = request.getHeader(principalRequestHeader);
		String domainIdentifier = request.getParameter("domain");
		if (domainIdentifier == null)	domainIdentifier = request.getHeader(domainRequestHeader);

		// TODO FMA support domain injection.
		if (authenticationHeader != null) {
			if (!authorizedAddresses.contains(request.getRemoteAddr())) {
				logger.error("SECURITY ALERT: Unauthorized header value '"
						+ authenticationHeader + "' from IP: "
						+ request.getRemoteAddr() + ":"
						+ request.getRemotePort());
				return null;
			} else {
				User foundUser = getPreAuthenticatedUser(authenticationHeader, domainIdentifier);
				if (foundUser == null) {
					logger.debug("No user was found with : " + authenticationHeader);
					logger.warn("PreAuthenticationHeader (SSO) is looking for someone who does not belong to the ldap domain anymore.");
					return null;
				}
				authenticationHeader = foundUser.getLsUuid();
			}
		}
		return authenticationHeader;
	}

	private User getPreAuthenticatedUser(String authenticationHeader,
			String domainIdentifier) {
		// Looking for a root user no matter the domain.
		User foundUser = userRepository.findByLogin(authenticationHeader);

		if (foundUser == null) {
			logger.debug("looking into ldap.");
			foundUser = ldapUserDetailsProvider.retrieveUser(domainIdentifier,
					authenticationHeader);
		}
		if (foundUser != null) {
			try {
				foundUser = accountFacade.findOrCreateUser(foundUser.getDomainId(), foundUser.getMail());
			} catch (BusinessException e) {
				logger.error(e.getMessage());
				throw new AuthenticationServiceException(
						"Could not create user account : "
								+ foundUser.getDomainId() + " : "
								+ foundUser.getMail(), e);
			}
		}
		return foundUser;
	}

	public void setPrincipalRequestHeader(String principalRequestHeader) {
		Assert.hasText(principalRequestHeader,
				"principalRequestHeader must not be empty or null");
		this.principalRequestHeader = principalRequestHeader;
	}

	public void setDomainRequestHeader(String domainRequestHeader) {
		Assert.hasText(domainRequestHeader,
				"domainRequestHeader must not be empty or null");
		this.domainRequestHeader = domainRequestHeader;
	}

	public void setAuthorizedAddresses(List<String> authorizedAddresses) {
		Assert.hasText(authorizedAddresses.toString(),
				"authorizedAddresses must not be empty or null");
		this.authorizedAddresses = authorizedAddresses;
	}

	public void setAccountFacade(AccountFacade accountFacade) {
		this.accountFacade = accountFacade;
	}

	public void setLdapUserDetailsProvider(
			LdapUserDetailsProvider userDetailsProvider) {
		this.ldapUserDetailsProvider = userDetailsProvider;
	}

	public void setUserRepository(UserRepository<User> userRepository) {
		this.userRepository = userRepository;
	}
}
