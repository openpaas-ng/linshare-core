/*
 * LinShare is an open source filesharing software, part of the LinPKI software
 * suite, developed by Linagora.
 * 
 * Copyright (C) 2015-2018 LINAGORA
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

package org.linagora.linshare.core.facade.webservice.user.dto;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlSeeAlso;

import org.linagora.linshare.core.domain.entities.RecipientFavourite;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonSubTypes.Type;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.google.common.base.Function;
import com.wordnik.swagger.annotations.ApiModel;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "type")
@JsonSubTypes({
		@Type(value = AutoCompleteResultDto.class, name = "simple"),
		@Type(value = UserAutoCompleteResultDto.class, name = "user"),
		@Type(value = ThreadMemberAutoCompleteResultDto.class, name = "threadmember"),
		@Type(value = ListAutoCompleteResultDto.class, name = "mailinglist"), })
@XmlRootElement(name = "AutoCompleteResult")
@XmlSeeAlso({ UserAutoCompleteResultDto.class,
		ThreadMemberAutoCompleteResultDto.class,
		ListAutoCompleteResultDto.class })
@ApiModel(value = "AutoCompleteResult", description = "Auto complete result object")
public class AutoCompleteResultDto {

	private String identifier;

	private String display;

	public AutoCompleteResultDto() {
		super();
	}

	public AutoCompleteResultDto(String identifier, String display) {
		this.identifier = identifier;
		this.display = display;
	}

	public AutoCompleteResultDto(RecipientFavourite recipientFavourite) {
		identifier = recipientFavourite.getRecipient();
		display = recipientFavourite.getRecipient();
	}

	public String getIdentifier() {
		return identifier;
	}

	public void setIdentifier(String identifier) {
		this.identifier = identifier;
	}

	public String getDisplay() {
		return display;
	}

	public void setDisplay(String display) {
		this.display = display;
	}

	public static Function<RecipientFavourite, AutoCompleteResultDto> toRFDto() {
		return new Function<RecipientFavourite, AutoCompleteResultDto>() {
			@Override
			public AutoCompleteResultDto apply(RecipientFavourite arg0) {
				return new AutoCompleteResultDto(arg0);
			}
		};
	}
}
