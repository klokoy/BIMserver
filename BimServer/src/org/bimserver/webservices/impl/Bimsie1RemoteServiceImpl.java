package org.bimserver.webservices.impl;

/******************************************************************************
 * Copyright (C) 2009-2013  BIMserver.org
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 * 
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *****************************************************************************/

import java.util.ArrayList;
import java.util.List;

import org.bimserver.BimServer;
import org.bimserver.database.BimserverDatabaseException;
import org.bimserver.database.DatabaseSession;
import org.bimserver.database.Query;
import org.bimserver.database.query.conditions.AttributeCondition;
import org.bimserver.database.query.conditions.Condition;
import org.bimserver.database.query.literals.StringLiteral;
import org.bimserver.interfaces.objects.SProfileDescriptor;
import org.bimserver.models.store.InternalServicePluginConfiguration;
import org.bimserver.models.store.StorePackage;
import org.bimserver.models.store.User;
import org.bimserver.shared.exceptions.ServerException;
import org.bimserver.shared.exceptions.UserException;
import org.bimserver.shared.interfaces.Bimsie1RemoteServiceInterfaceAdaptor;
import org.bimserver.webservices.ServiceMap;

public class Bimsie1RemoteServiceImpl extends Bimsie1RemoteServiceInterfaceAdaptor {
	private ServiceMap serviceMapInterface;
	private BimServer bimServer;

	public Bimsie1RemoteServiceImpl(BimServer bimServer, ServiceMap serviceMapInterface) {
		this.bimServer = bimServer;
		this.serviceMapInterface = serviceMapInterface;
	}
	
	@Override
	public List<SProfileDescriptor> getPublicProfiles(String serviceIdentifier) throws UserException, ServerException {
		return new ArrayList<SProfileDescriptor>();
	}

	@Override
	public List<SProfileDescriptor> getPrivateProfiles(String serviceIdentifier, String token) throws UserException, ServerException {
		DatabaseSession session = serviceMapInterface.getBimServer().getDatabase().createSession();
		List<SProfileDescriptor> descriptors = new ArrayList<SProfileDescriptor>();
		try {
			Condition condition = new AttributeCondition(StorePackage.eINSTANCE.getUser_Token(), new StringLiteral(token));
			User user = session.querySingle(condition, User.class, Query.getDefault());
			if (user != null) {
				for (InternalServicePluginConfiguration internalServicePluginConfiguration : user.getUserSettings().getServices()) {
					if (internalServicePluginConfiguration.getPluginDescriptor().getPluginClassName().equals(serviceIdentifier)) {
						SProfileDescriptor sProfileDescriptor = new SProfileDescriptor();
						descriptors.add(sProfileDescriptor);
						
						sProfileDescriptor.setIdentifier("" + internalServicePluginConfiguration.getOid());
						sProfileDescriptor.setName(internalServicePluginConfiguration.getName());
						sProfileDescriptor.setDescription(internalServicePluginConfiguration.getDescription());
						sProfileDescriptor.setPublicProfile(false);
					}
				}
			}
		} catch (BimserverDatabaseException e) {
			e.printStackTrace();
		} finally {
			session.close();
		}
		return descriptors;
	}
	
	@Override
	public void newRevision(Long poid, Long roid, Long soid, String serviceIdentifier, String profileIdentifier, String token, String apiUrl) throws UserException, ServerException {
		bimServer.getInternalServicesManager().getLocalRemoteServiceInterface(serviceIdentifier).newRevision(poid, roid, soid, serviceIdentifier, profileIdentifier, token, apiUrl);
	}
}
