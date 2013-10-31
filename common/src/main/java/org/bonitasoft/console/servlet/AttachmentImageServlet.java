/**
 * Copyright (C) 2009 BonitaSoft S.A.
 * BonitaSoft, 31 rue Gustave Eiffel - 38000 Grenoble
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 2.0 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.bonitasoft.console.servlet;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.activation.FileTypeMap;
import javax.activation.MimetypesFileTypeMap;
import javax.security.auth.login.LoginContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.bonitasoft.console.security.client.privileges.RuleType;
import org.bonitasoft.console.security.client.users.User;
import org.bonitasoft.console.security.server.accessor.PropertiesFactory;
import org.bonitasoft.console.security.server.constants.WebBonitaConstants;
import org.ow2.bonita.facade.QueryDefinitionAPI;
import org.ow2.bonita.facade.QueryRuntimeAPI;
import org.ow2.bonita.facade.runtime.AttachmentInstance;
import org.ow2.bonita.facade.runtime.InitialAttachment;
import org.ow2.bonita.facade.uuid.ActivityInstanceUUID;
import org.ow2.bonita.facade.uuid.ProcessDefinitionUUID;
import org.ow2.bonita.facade.uuid.ProcessInstanceUUID;
import org.ow2.bonita.light.LightProcessInstance;
import org.ow2.bonita.util.AccessorUtil;

/**
 * Servlet allowing to download process instances attachments
 * 
 * @author Anthony Birembaut
 */
public class AttachmentImageServlet extends AttachmentDownloadServlet {
    
    /**
     * UID
     */
    private static final long serialVersionUID = -2397573068771431608L;
    
    /**
     * Logger
     */
    private static final Logger LOGGER = Logger.getLogger(AttachmentImageServlet.class.getName());
    
    /**
     * {@inheritDoc}
     */
    @Override
    protected void doGet(final HttpServletRequest request, final HttpServletResponse response) throws ServletException {
        
        final String taskUUIDStr = request.getParameter(TASK_ID_PARAM);
        final String processUUIDStr = request.getParameter(PROCESS_ID_PARAM);
        final String instanceUUIDStr = request.getParameter(INSTANCE_ID_PARAM);
        boolean isCurrentValue = false;
		final String isCurrentValueStr = request.getParameter(CURRENT_VALUE_PARAM);
		if (isCurrentValueStr != null) {
			isCurrentValue = Boolean.parseBoolean(isCurrentValueStr);
		}
        final String attachmentName = request.getParameter(ATTACHMENT_PARAM);
        final String attachmentPath = request.getParameter(ATTACHMENT_PATH_PARAM);
        String attachmentFileName = request.getParameter(ATTACHMENT_FILE_NAME_PARAM);
        final QueryRuntimeAPI queryRuntimeAPI = AccessorUtil.getQueryRuntimeAPI();
        final QueryDefinitionAPI queryDefinitionAPI = AccessorUtil.getQueryDefinitionAPI();
        byte[] attachment = null;
        String contentType = null;
        if (attachmentPath != null){
            final File file = new File(attachmentPath);

            try {
                final File tmpDir = PropertiesFactory.getTenancyProperties().getDomainCommonTempFolder();
                if (!file.getCanonicalPath().startsWith(tmpDir.getCanonicalPath())) {
                    throw new IOException();
                }
            } catch (final IOException e) {
                final String errorMessage = "Error while getting the file " + attachmentPath + " For security reasons, access to paths other than " + WebBonitaConstants.BONITA_HOME + "/" + WebBonitaConstants.platformCommonTempSubFolderPath + " is restricted";
                if (LOGGER.isLoggable(Level.SEVERE)) {
                    LOGGER.log(Level.SEVERE, errorMessage, e);
                }
                throw new ServletException(errorMessage);
            }
            int fileLength = 0;
            if (file.length() > Integer.MAX_VALUE) {
                throw new ServletException("file " + attachmentPath + " too big !");
            } else {
                fileLength = (int)file.length();
            }
            if (attachmentFileName == null) {
                attachmentFileName = file.getName();
            }
            final FileTypeMap mimetypesFileTypeMap = new MimetypesFileTypeMap();
            contentType = mimetypesFileTypeMap.getContentType(file);
            attachment = getFileContent(file, fileLength, attachmentPath);
        } else if (attachmentName != null && attachmentName.length() > 0) {
            LoginContext loginContext = null;
            try {
                loginContext = ServletLoginUtils.engineLogin(request);
                final User user = ServletLoginUtils.getUser(request);
                if (taskUUIDStr != null) {
                    final ActivityInstanceUUID activityInstanceUUID = new ActivityInstanceUUID(taskUUIDStr);
                    final ProcessInstanceUUID processInstanceUUID = activityInstanceUUID.getProcessInstanceUUID();
                    final LightProcessInstance processInstance = queryRuntimeAPI.getLightProcessInstance(processInstanceUUID);
                    final Set<String> involvedUsers = new HashSet<String>();
                    ProcessInstanceUUID rootProcessInstanceUUID = processInstance.getRootInstanceUUID();
                    if (processInstanceUUID.equals(rootProcessInstanceUUID)) {
                        involvedUsers.add(processInstance.getStartedBy());
                        involvedUsers.addAll(queryRuntimeAPI.getInvolvedUsersOfProcessInstance(processInstanceUUID));
                    } else {
                        final LightProcessInstance rootProcessInstance = queryRuntimeAPI.getLightProcessInstance(rootProcessInstanceUUID);
                        involvedUsers.add(rootProcessInstance.getStartedBy());
                        involvedUsers.addAll(queryRuntimeAPI.getInvolvedUsersOfProcessInstance(rootProcessInstanceUUID));
                    }
                    if (user.isAdmin() || (involvedUsers.contains(user.getUsername()) && user.isAllowed(RuleType.PROCESS_READ, processInstanceUUID.getProcessDefinitionUUID().getValue()))) {
                        final AttachmentInstance attachmentInstance = queryRuntimeAPI.getLastAttachment(activityInstanceUUID.getProcessInstanceUUID(), attachmentName, activityInstanceUUID);
                        attachmentFileName = attachmentInstance.getFileName();
                        if (attachmentInstance.getMetaData() != null) {
                            contentType = attachmentInstance.getMetaData().get("content-type");
                        }
                        attachment = queryRuntimeAPI.getAttachmentValue(attachmentInstance);
                    } else {
                        final String errorMessage = "You do not have the privileges to view this attachment";
                        if (LOGGER.isLoggable(Level.SEVERE)) {
                            LOGGER.log(Level.SEVERE, errorMessage);
                        }
                        throw new ServletException(errorMessage);
                    }
                } else if (processUUIDStr != null){
                    final ProcessDefinitionUUID processDefinitionUUID = new ProcessDefinitionUUID(processUUIDStr);
                    if (user.isAdmin() || user.isAllowed(RuleType.PROCESS_READ, processDefinitionUUID.getValue())) {
                        final InitialAttachment initialAttachment = queryDefinitionAPI.getProcessAttachment(processDefinitionUUID, attachmentName);
                        attachment = initialAttachment.getContent();
                        attachmentFileName = initialAttachment.getFileName();
                        if (initialAttachment.getMetaData() != null) {
                            contentType = initialAttachment.getMetaData().get("content-type");
                        }
                    } else {
                        final String errorMessage = "You do not have the privileges to view this attachment";
                        if (LOGGER.isLoggable(Level.SEVERE)) {
                            LOGGER.log(Level.SEVERE, errorMessage);
                        }
                        throw new ServletException(errorMessage);
                    }
                } else {
                    final ProcessInstanceUUID processInstanceUUID = new ProcessInstanceUUID(instanceUUIDStr);
                    final LightProcessInstance processInstance = queryRuntimeAPI.getLightProcessInstance(processInstanceUUID);
                    final Set<String> involvedUsers = new HashSet<String>();
                    ProcessInstanceUUID rootProcessInstanceUUID = processInstance.getRootInstanceUUID();
                    if (processInstanceUUID.equals(rootProcessInstanceUUID)) {
                        involvedUsers.add(processInstance.getStartedBy());
                        involvedUsers.addAll(queryRuntimeAPI.getInvolvedUsersOfProcessInstance(processInstanceUUID));
                    } else {
                        final LightProcessInstance rootProcessInstance = queryRuntimeAPI.getLightProcessInstance(rootProcessInstanceUUID);
                        involvedUsers.add(rootProcessInstance.getStartedBy());
                        involvedUsers.addAll(queryRuntimeAPI.getInvolvedUsersOfProcessInstance(rootProcessInstanceUUID));
                    }
                    if (user.isAdmin() || (involvedUsers.contains(user.getUsername()) && user.isAllowed(RuleType.PROCESS_READ, processInstanceUUID.getProcessDefinitionUUID().getValue()))) {
                        AttachmentInstance attachmentInstance;
                        if (isCurrentValue) {
                        	attachmentInstance = queryRuntimeAPI.getLastAttachment(processInstanceUUID, attachmentName);
                        } else {
                        	attachmentInstance = queryRuntimeAPI.getLastAttachment(processInstanceUUID, attachmentName, processInstance.getStartedDate());
                        }
                        attachmentFileName = attachmentInstance.getFileName();
                        if (attachmentInstance.getMetaData() != null) {
                            contentType = attachmentInstance.getMetaData().get("content-type");
                        }
                        attachment = queryRuntimeAPI.getAttachmentValue(attachmentInstance);
                    } else {
                        final String errorMessage = "You do not have the privileges to view this attachment";
                        if (LOGGER.isLoggable(Level.SEVERE)) {
                            LOGGER.log(Level.SEVERE, errorMessage);
                        }
                        throw new ServletException(errorMessage);
                    }
                }
                if (contentType == null && attachmentFileName != null && attachmentFileName.lastIndexOf(".") >= 0) {
                    contentType = "image/" +  attachmentFileName.substring(attachmentFileName.lastIndexOf("."));
                }
            } catch (final Exception e) {
                final String errorMessage = "Error while getting the attachment " + attachmentName + " for task " + taskUUIDStr + " instance " + instanceUUIDStr + " process " + processUUIDStr;
                if (LOGGER.isLoggable(Level.SEVERE)) {
                    LOGGER.log(Level.SEVERE, errorMessage, e);
                }
                throw new ServletException(e.getMessage(), e);
            } finally {
            	ServletLoginUtils.engineLogout(loginContext);
            }
        } else {
            final String errorMessage = "Error while getting the attachment. attachmentName parameter null or empty.";
            if (LOGGER.isLoggable(Level.SEVERE)) {
                LOGGER.log(Level.SEVERE, errorMessage);
            }
            throw new ServletException(errorMessage);
        }
        if (contentType != null) {
            response.setContentType(contentType);
        }
        response.setCharacterEncoding("UTF-8");
        if (attachmentFileName != null) {
            try {
                final String encodedfileName = URLEncoder.encode(attachmentFileName, "UTF-8");
                final String userAgent = request.getHeader("User-Agent");
                if (userAgent != null && userAgent.contains("Firefox")) {
                    response.setHeader("Content-Disposition", "inline; filename*=UTF-8''" + encodedfileName);
                } else {
                    response.setHeader("Content-Disposition", "inline; filename=\"" + encodedfileName.replaceAll("\\+", " ") + "\"; filename*=UTF-8''" + encodedfileName);
                }
                if (attachment != null) {
                    response.setContentLength (attachment.length);
                    OutputStream out = null;
                    try {
                        out = response.getOutputStream();
                        out.write(attachment);
                    } finally {
                        if (out != null) {
                            out.close();
                        }
                    }
                }
            } catch (final IOException e) {
                if (LOGGER.isLoggable(Level.SEVERE)) {
                    LOGGER.log(Level.SEVERE, "Error while generating the response." , e);
                }
                throw new ServletException(e.getMessage(), e);
            }
        }
    }
}
