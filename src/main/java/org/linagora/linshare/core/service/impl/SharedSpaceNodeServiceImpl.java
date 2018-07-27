/*
 * LinShare is an open source filesharing software, part of the LinPKI software
 * suite, developed by Linagora.
 * 
 * Copyright (C) 2018 LINAGORA
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
package org.linagora.linshare.core.service.impl;

import java.util.List;

import org.jsoup.helper.Validate;
import org.linagora.linshare.core.business.service.SharedSpaceMemberBusinessService;
import org.linagora.linshare.core.business.service.SharedSpaceNodeBusinessService;
import org.linagora.linshare.core.domain.constants.AuditLogEntryType;
import org.linagora.linshare.core.domain.constants.LogAction;
import org.linagora.linshare.core.domain.entities.Account;
import org.linagora.linshare.core.exception.BusinessErrorCode;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.rac.SharedSpaceNodeResourceAccessControl;
import org.linagora.linshare.core.service.LogEntryService;
import org.linagora.linshare.core.service.SharedSpaceMemberService;
import org.linagora.linshare.core.service.SharedSpaceNodeService;
import org.linagora.linshare.core.service.SharedSpaceRoleService;
import org.linagora.linshare.mongo.entities.SharedSpaceMember;
import org.linagora.linshare.mongo.entities.SharedSpaceNode;
import org.linagora.linshare.mongo.entities.SharedSpaceRole;
import org.linagora.linshare.mongo.entities.light.GenericLightEntity;
import org.linagora.linshare.mongo.entities.logs.SharedSpaceNodeAuditLogEntry;

import com.google.common.collect.Lists;

public class SharedSpaceNodeServiceImpl extends GenericServiceImpl<Account, SharedSpaceNode>
		implements SharedSpaceNodeService {

	private final SharedSpaceNodeBusinessService sharedSpaceNodeBusinessService;

	private final SharedSpaceMemberBusinessService memberBusinessService;

	private final SharedSpaceMemberService sharedSpaceMemberService;

	private final SharedSpaceRoleService ssRoleService;

	private final LogEntryService logEntryService;

	public SharedSpaceNodeServiceImpl(SharedSpaceNodeBusinessService sharedSpaceNodeBusinessService,
			SharedSpaceNodeResourceAccessControl sharedSpaceNodeResourceAccessControl,
			SharedSpaceMemberBusinessService memberBusinessService,
			SharedSpaceMemberService sharedSpaceMemberService, SharedSpaceRoleService ssRoleService,
			LogEntryService logEntryService) {
		super(sharedSpaceNodeResourceAccessControl);
		this.sharedSpaceNodeBusinessService = sharedSpaceNodeBusinessService;
		this.sharedSpaceMemberService = sharedSpaceMemberService;
		this.ssRoleService = ssRoleService;
		this.memberBusinessService = memberBusinessService;
		this.logEntryService = logEntryService;
	}

	@Override
	public SharedSpaceNode find(Account authUser, Account actor, String uuid) throws BusinessException {
		preChecks(authUser, actor);
		Validate.notEmpty(uuid, "Missing required shared space node uuid.");
		SharedSpaceNode found = sharedSpaceNodeBusinessService.find(uuid);
		if (found == null) {
			throw new BusinessException(BusinessErrorCode.WORK_GROUP_NOT_FOUND,
					"The shared space node with uuid: " + uuid + " is not found");
		}
		checkReadPermission(authUser, actor, SharedSpaceNode.class, BusinessErrorCode.WORK_GROUP_FORBIDDEN, found);
		return found;
	}

	@Override
	public SharedSpaceNode create(Account authUser, Account actor, SharedSpaceNode node) throws BusinessException {
		preChecks(authUser, actor);
		Validate.notNull(node, "Missing required input shared space node.");
		Validate.notNull(node.getNodeType(), "you must set the node type");
		checkCreatePermission(authUser, actor, SharedSpaceNode.class, BusinessErrorCode.WORK_GROUP_FORBIDDEN, null);
		SharedSpaceNode created = sharedSpaceNodeBusinessService.create(node);
		SharedSpaceRole role = ssRoleService.getAdmin(authUser, actor);
		SharedSpaceNodeAuditLogEntry log = new SharedSpaceNodeAuditLogEntry(authUser, actor, LogAction.CREATE,
				AuditLogEntryType.SHARED_SPACE_NODE, created);
		logEntryService.insert(log);
		sharedSpaceMemberService.createWithoutCheckPermission(authUser, authUser,
				new GenericLightEntity(node.getUuid(), node.getName()),
				new GenericLightEntity(role.getUuid(), role.getName()),
				new GenericLightEntity(actor.getLsUuid(), actor.getFullName()));
		return created;
	}

	@Override
	public SharedSpaceNode delete(Account authUser, Account actor, SharedSpaceNode node) throws BusinessException {
		preChecks(authUser, actor);
		Validate.notNull(node, "missing required node to delete.");
		Validate.notEmpty(node.getUuid(), "missing required node uuid to delete");
		SharedSpaceNode foundedNodeTodel = find(authUser, actor, node.getUuid());
		checkDeletePermission(authUser, actor, SharedSpaceNode.class, BusinessErrorCode.WORK_GROUP_FORBIDDEN,
				foundedNodeTodel);
		sharedSpaceNodeBusinessService.delete(foundedNodeTodel);
		SharedSpaceNodeAuditLogEntry log = new SharedSpaceNodeAuditLogEntry(authUser, actor, LogAction.DELETE,
				AuditLogEntryType.SHARED_SPACE_NODE, foundedNodeTodel);
		logEntryService.insert(log);
		sharedSpaceMemberService.deleteAllMembers(authUser, actor, foundedNodeTodel.getUuid());
		return foundedNodeTodel;
	}

	@Override
	public SharedSpaceNode update(Account authUser, Account actor, SharedSpaceNode nodeToUpdate)
			throws BusinessException {
		Validate.notNull(nodeToUpdate, "nodeToUpdate must be set.");
		Validate.notEmpty(nodeToUpdate.getUuid(), "shared space node uuid to update must be set.");
		SharedSpaceNode node = find(authUser, actor, nodeToUpdate.getUuid());
		checkUpdatePermission(authUser, actor, SharedSpaceNode.class, BusinessErrorCode.WORK_GROUP_FORBIDDEN,
				nodeToUpdate);
		SharedSpaceNode updated = sharedSpaceNodeBusinessService.update(node, nodeToUpdate);
		memberBusinessService.updateNestedNode(updated);
		SharedSpaceNodeAuditLogEntry log = new SharedSpaceNodeAuditLogEntry(authUser, actor, LogAction.UPDATE,
				AuditLogEntryType.SHARED_SPACE_NODE, node);
		log.setResourceUpdated(updated);
		logEntryService.insert(log);
		return updated;
	}

	@Override
	public List<SharedSpaceNode> findAll(Account authUser) {
		preChecks(authUser, authUser);
		checkListPermission(authUser, authUser, SharedSpaceNode.class, BusinessErrorCode.WORK_GROUP_FORBIDDEN, null);
		return sharedSpaceNodeBusinessService.findAll();
	}

	@Override
	public List<SharedSpaceNode> findAllByAccount(Account authUser, Account actor) {
		preChecks(authUser, actor);
		checkListPermission(authUser, actor, SharedSpaceNode.class, BusinessErrorCode.WORK_GROUP_FORBIDDEN, null);
		List<SharedSpaceMember> actorMemberShips = sharedSpaceMemberService.findAllByAccount(authUser, actor,
				actor.getLsUuid());
		List<SharedSpaceNode> actorNodes = Lists.newArrayList();
		for (SharedSpaceMember sharedSpaceMember : actorMemberShips) {
			actorNodes.add(find(authUser, actor, sharedSpaceMember.getNode().getUuid()));
		}
		return actorNodes;
	}

	@Override
	public List<SharedSpaceMember> findAllMembers(Account authUser, Account actor, String sharedSpaceNodeUuid) {
		return sharedSpaceMemberService.findAll(authUser, actor, sharedSpaceNodeUuid);
	}

	@Override
	public List <SharedSpaceNode> searchByName(Account authUser, Account actor, String name) throws BusinessException {
		preChecks(authUser, actor);
		Validate.notEmpty(name, "Missing required shared space node name.");
		List<SharedSpaceNode> founds= sharedSpaceNodeBusinessService.searchByName(name);
		if(founds.isEmpty()) {
			findAll(authUser);
		}
		checkListPermission(authUser, actor, SharedSpaceNode.class, BusinessErrorCode.WORK_GROUP_FORBIDDEN, null);
		return founds;
	}

	@Override
	public List<SharedSpaceNode> findAllNodesBySSMember(Account authUser, String memberName) {
		List<SharedSpaceMember> ssmembers = sharedSpaceMemberService.findByMemberName(authUser, authUser,
				memberName);
		//TODO : to replace by query methods in mongoRepository for more performances.
		List<SharedSpaceNode> nodes = Lists.newArrayList();
		for (SharedSpaceMember member : ssmembers) {
			nodes.add(find(authUser, authUser, member.getNode().getUuid()));
		}
		return nodes;
	}
}
