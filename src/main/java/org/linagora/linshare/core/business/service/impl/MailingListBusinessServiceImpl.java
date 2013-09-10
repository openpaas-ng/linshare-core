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

package org.linagora.linshare.core.business.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.linagora.linshare.core.business.service.MailingListBusinessService;
import org.linagora.linshare.core.domain.entities.MailingList;
import org.linagora.linshare.core.domain.entities.MailingListContact;
import org.linagora.linshare.core.domain.entities.User;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.repository.MailingListContactRepository;
import org.linagora.linshare.core.repository.MailingListRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MailingListBusinessServiceImpl implements MailingListBusinessService {
	
	
    private static final Logger logger = LoggerFactory.getLogger(MailingListBusinessServiceImpl.class);
	private final MailingListRepository mailingListRepository;
	private final MailingListContactRepository mailingListContactRepository;

	public MailingListBusinessServiceImpl(MailingListRepository mailingListRepository, MailingListContactRepository mailingListContactRepository)
	{
		super();
		this.mailingListRepository=mailingListRepository;
		this.mailingListContactRepository=mailingListContactRepository;
	}
	
	@Override
	public void deleteContact(MailingList mailingList, String mail) throws BusinessException { 
		MailingListContact mailToDelete = retrieveContact(mailingList, mail);
    	if (mailToDelete == null) {
    		logger.error("mail not found");
    	} else {
    		mailingListContactRepository.delete(mailToDelete);
    	}
	}
	
    @Override
    public MailingListContact retrieveContact(MailingList mailingList, String mail) {
    	return mailingListContactRepository.findByMail(mailingList, mail);
    }
	
    @Override
    public MailingList createList(MailingList mailingList) throws BusinessException{
        List<MailingList> myList = mailingListRepository.findAllListWhereOwner(mailingList.getOwner());
        for(MailingList currentList : myList){
        	if(currentList.getIdentifier().equals(mailingList.getIdentifier())){
        		logger.error("identifier already exists !");
        		return null;
        	}
        }
    	MailingList createdList = mailingListRepository.create(mailingList);
        return createdList;
    }
    
    @Override
    public MailingList retrieveList(String uuid) {
    	return mailingListRepository.findByUuid(uuid);
    }
    
    @Override
    public MailingList findListByIdentifier(User owner, String identifier){
    	return mailingListRepository.findByIdentifier(owner, identifier);
    }
    
    @Override
    public List<MailingList> findAllList() {
    	List<MailingList> myList = new ArrayList<MailingList>();
    	myList = mailingListRepository.findAll();
    	return myList;
    }
    
    @Override
    public List<MailingList> findAllListByUser(User user) {
    	return mailingListRepository.findallMyList(user);
    }
    
    @Override
    public List<MailingList> findAllMyList(User user) {
    	return mailingListRepository.findAllListWhereOwner(user);
    }
    
    @Override
    public void deleteList(String uuid) throws BusinessException{
    	MailingList listToDelete = retrieveList(uuid);
    	if (listToDelete == null) {
    		logger.error("List not found");
    	} else {
    		logger.debug("List to delete: "+listToDelete.getIdentifier());
    		mailingListRepository.delete(listToDelete);
    	}
    }
    
    @Override
    public void updateList(MailingList listToUpdate) throws BusinessException{
    	
    	MailingList list = retrieveList(listToUpdate.getUuid());
    	list.setIdentifier(listToUpdate.getIdentifier());
    	list.setDescription(listToUpdate.getDescription());
    	list.setPublic(listToUpdate.isPublic());
    	list.setDomain(listToUpdate.getDomain());
    	list.setOwner(listToUpdate.getOwner());
    
    	mailingListRepository.update(list);
    }
    
    @Override
    public void updateContact(MailingList list, MailingListContact contactToUpdate) throws BusinessException {
    	MailingListContact contact = retrieveContact(list, contactToUpdate.getMail());
    	contact.setMails(contactToUpdate.getMail());
    	contact.setDisplay(contactToUpdate.getDisplay());
    	mailingListContactRepository.update(contact);
    }
    
}
