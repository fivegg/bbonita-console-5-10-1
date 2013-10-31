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
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package org.bonitasoft.console.servlet;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.security.auth.login.LoginContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.bonitasoft.console.common.exception.NoCredentialsInSessionException;
import org.bonitasoft.console.security.client.privileges.RuleType;
import org.bonitasoft.console.security.client.users.User;
import org.bonitasoft.console.security.server.accessor.PropertiesFactory;
import org.bonitasoft.console.security.server.constants.WebBonitaConstants;
import org.ow2.bonita.facade.QueryDefinitionAPI;
import org.ow2.bonita.facade.QueryRuntimeAPI;
import org.ow2.bonita.facade.exception.InstanceNotFoundException;
import org.ow2.bonita.facade.runtime.AttachmentInstance;
import org.ow2.bonita.facade.runtime.InitialAttachment;
import org.ow2.bonita.facade.uuid.ActivityInstanceUUID;
import org.ow2.bonita.facade.uuid.DocumentUUID;
import org.ow2.bonita.facade.uuid.ProcessDefinitionUUID;
import org.ow2.bonita.facade.uuid.ProcessInstanceUUID;
import org.ow2.bonita.light.LightProcessInstance;
import org.ow2.bonita.util.AccessorUtil;

/**
 * Servlet allowing to download process instances attachments
 * 
 * @author Anthony Birembaut
 */
public class AttachmentDownloadServlet extends HttpServlet {

    /**
     * UID
     */
    private static final long serialVersionUID = 5209516978177786895L;

    /**
     * JAAS Store login context
     */
    public static final String JAAS_STORE_LOGIN_CONTEXT = "BonitaStore";

    /**
     * user credentials param key inside the session
     */
    public static final String USER_CREDENTIALS_SESSION_PARAM_KEY = "userCredentials";

    /**
     * task id : indicate the task id for which the form has to be displayed
     */
    public static final String TASK_ID_PARAM = "task";

    /**
     * process id : indicate the process for witch the form has to be displayed
     */
    public static final String PROCESS_ID_PARAM = "process";

    /**
     * process instance id : indicate the instance for witch the form has to be displayed
     */
    public static final String INSTANCE_ID_PARAM = "instance";

    /**
     * document id of the document to download
     */
    public static final String DOCUMENT_ID_PARAM = "document";

    /**
     * filename of the document to download
     */
    public static final String DOCUMENT_FILENAME_PARAM = "documentFilename";

    /**
     * current value : indicates if the required attachment version is the current version
     */
    public static final String CURRENT_VALUE_PARAM = "currentValue";

    /**
     * attachment : indicate the name of the process attachment
     */
    public static final String ATTACHMENT_PARAM = "attachment";

    /**
     * attachment : indicate the path of the process attachment
     */
    public static final String ATTACHMENT_PATH_PARAM = "attachmentPath";

    /**
     * attachment : indicate the file name of the process attachment
     */
    public static final String ATTACHMENT_FILE_NAME_PARAM = "attachmentFileName";

    /**
     * recap : indicate recap or not
     */
    public static final String RECAP = "recap";

    /**
     * Logger
     */
    private static final Logger LOGGER = Logger.getLogger(AttachmentDownloadServlet.class.getName());

    /**
     * {@inheritDoc}
     */
    @Override
    protected void doGet(final HttpServletRequest request, final HttpServletResponse response) throws ServletException {

        final String taskUUIDStr = request.getParameter(TASK_ID_PARAM);
        final String processUUIDStr = request.getParameter(PROCESS_ID_PARAM);
        final String instanceUUIDStr = request.getParameter(INSTANCE_ID_PARAM);
        final String documentIDstr = request.getParameter(DOCUMENT_ID_PARAM);
        final String documentFileNamestr = request.getParameter(DOCUMENT_FILENAME_PARAM);
        boolean isCurrentValue = false;
        final String isCurrentValueStr = request.getParameter(CURRENT_VALUE_PARAM);
        if (isCurrentValueStr != null) {
            isCurrentValue = Boolean.parseBoolean(isCurrentValueStr);
        }
        final String attachmentName = request.getParameter(ATTACHMENT_PARAM);
        final String attachmentPath = request.getParameter(ATTACHMENT_PATH_PARAM);
        String attachmentFileName = request.getParameter(ATTACHMENT_FILE_NAME_PARAM);
        final String isRecap = request.getParameter(RECAP);
        final QueryRuntimeAPI queryRuntimeAPI = AccessorUtil.getQueryRuntimeAPI();
        final QueryDefinitionAPI queryDefinitionAPI = AccessorUtil.getQueryDefinitionAPI();
        byte[] attachment = null;
        if (attachmentPath != null) {
            if (LOGGER.isLoggable(Level.FINE)) {
                LOGGER.log(Level.FINE, "attachmentPath: " + attachmentPath);
            }
            final File file = new File(attachmentPath);
            try {
                final File tmpDir = PropertiesFactory.getTenancyProperties().getDomainCommonTempFolder();
                if (!file.getCanonicalPath().startsWith(tmpDir.getCanonicalPath())) {
                    throw new IOException();
                }
            } catch (final IOException e) {
                final String errorMessage = "Error while getting the file " + attachmentPath + " For security reasons, access to paths other than "
                        + WebBonitaConstants.BONITA_HOME + "/" + WebBonitaConstants.platformCommonTempSubFolderPath + " is restricted";
                if (LOGGER.isLoggable(Level.SEVERE)) {
                    LOGGER.log(Level.SEVERE, errorMessage, e);
                }
                throw new ServletException(errorMessage);
            }
            int fileLength = 0;
            if (file.length() > Integer.MAX_VALUE) {
                throw new ServletException("file " + attachmentPath + " too big !");
            } else {
                fileLength = (int) file.length();
            }
            if (attachmentFileName == null) {
                attachmentFileName = file.getName();
            }
            attachment = getFileContent(file, fileLength, attachmentPath);
        } else {
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
                    if (user.isAdmin()
                            || (involvedUsers.contains(user.getUsername()) && user.isAllowed(RuleType.PROCESS_READ, processInstanceUUID
                                    .getProcessDefinitionUUID().getValue()))) {
                        final AttachmentInstance attachmentInstance = queryRuntimeAPI.getLastAttachment(processInstanceUUID, attachmentName,
                                activityInstanceUUID);
                        attachment = queryRuntimeAPI.getAttachmentValue(attachmentInstance);
                        attachmentFileName = attachmentInstance.getFileName();
                    } else {
                        final String errorMessage = "You do not have the privileges to view this attachment";
                        if (LOGGER.isLoggable(Level.SEVERE)) {
                            LOGGER.log(Level.SEVERE, errorMessage);
                        }
                        throw new ServletException(errorMessage);
                    }
                } else if (processUUIDStr != null) {
                    final ProcessDefinitionUUID processDefinitionUUID = new ProcessDefinitionUUID(processUUIDStr);
                    if (user.isAdmin() || user.isAllowed(RuleType.PROCESS_READ, processDefinitionUUID.getValue())) {
                        if (documentIDstr != null) {
                            attachment = queryRuntimeAPI.getDocumentContent(new DocumentUUID(documentIDstr));
                            attachmentFileName = documentFileNamestr;
                        } else {
                            final InitialAttachment initialAttachment = queryDefinitionAPI.getProcessAttachment(processDefinitionUUID, attachmentName);
                            attachment = initialAttachment.getContent();
                            attachmentFileName = initialAttachment.getFileName();
                        }
                    } else {
                        final String errorMessage = "You do not have the privileges to view this attachment";
                        if (LOGGER.isLoggable(Level.SEVERE)) {
                            LOGGER.log(Level.SEVERE, errorMessage);
                        }
                        throw new ServletException(errorMessage);
                    }
                } else if (instanceUUIDStr != null && documentIDstr == null) {
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
                    if (user.isAdmin()
                            || (involvedUsers.contains(user.getUsername()) && user.isAllowed(RuleType.PROCESS_READ, processInstanceUUID
                                    .getProcessDefinitionUUID().getValue()))) {
                        AttachmentInstance attachmentInstance;
                        if (Boolean.TRUE.toString().equals(isRecap)) {
                            attachmentInstance = queryRuntimeAPI.getLastAttachment(processInstanceUUID, attachmentName);
                        } else {
                            if (isCurrentValue) {
                                attachmentInstance = queryRuntimeAPI.getLastAttachment(processInstanceUUID, attachmentName);
                            } else {
                                attachmentInstance = queryRuntimeAPI.getLastAttachment(processInstanceUUID, attachmentName, processInstance.getStartedDate());
                            }
                        }
                        attachment = queryRuntimeAPI.getAttachmentValue(attachmentInstance);
                        attachmentFileName = attachmentInstance.getFileName();
                    } else {
                        final String errorMessage = "You do not have the privileges to view this attachment";
                        if (LOGGER.isLoggable(Level.SEVERE)) {
                            LOGGER.log(Level.SEVERE, errorMessage);
                        }
                        throw new ServletException(errorMessage);
                    }
                } else {
                    // Download a document based on its ID
                    final ProcessInstanceUUID processInstanceUUID = new ProcessInstanceUUID(instanceUUIDStr);
                    try {
                        final Set<String> involvedUsers = queryRuntimeAPI.getInvolvedUsersOfProcessInstance(processInstanceUUID);
                        if (user.isAdmin()
                                || (involvedUsers.contains(user.getUsername()) && user.isAllowed(RuleType.PROCESS_READ, processInstanceUUID
                                        .getProcessDefinitionUUID().getValue()))) {
                            attachment = queryRuntimeAPI.getDocumentContent(new DocumentUUID(documentIDstr));
                            attachmentFileName = documentFileNamestr;
                        } else {
                            final String errorMessage = "You do not have the privileges to view this document";
                            if (LOGGER.isLoggable(Level.SEVERE)) {
                                LOGGER.log(Level.SEVERE, errorMessage);
                            }
                            throw new ServletException(errorMessage);
                        }
                    } catch (InstanceNotFoundException e) {
                        // The instance may have been deleted. We keep allowing document download.
                        attachment = queryRuntimeAPI.getDocumentContent(new DocumentUUID(documentIDstr));
                        attachmentFileName = documentFileNamestr;
                    }
                }
            } catch (NoCredentialsInSessionException e) {
                if (LOGGER.isLoggable(Level.SEVERE)) {
                    LOGGER.log(Level.SEVERE, e.getMessage(), e);
                }
                String refer = request.getParameter("refer");
                String newURL = "login.jsp?redirectUrl=" + URLEncoder.encode(refer);
                try {
                    response.sendRedirect(newURL);
                } catch (IOException e1) {
                    final String errorMessage = "Error while redircting to the URL: " + newURL;
                    if (LOGGER.isLoggable(Level.SEVERE)) {
                        LOGGER.log(Level.SEVERE, errorMessage, e);
                    }
                    throw new ServletException(errorMessage, e);
                }

            } catch (final Exception e) {
                final String errorMessage = "Error while getting the attachment " + attachmentName + " for task: " + taskUUIDStr + " or instance: "
                        + instanceUUIDStr + " or process: " + processUUIDStr;
                if (LOGGER.isLoggable(Level.SEVERE)) {
                    LOGGER.log(Level.SEVERE, errorMessage, e);
                }
                throw new ServletException(errorMessage, e);
            } finally {
                ServletLoginUtils.engineLogout(loginContext);
            }
        }
        response.setContentType("application/octet-stream");
        response.setCharacterEncoding("UTF-8");
        try {
            final String encodedfileName = URLEncoder.encode(attachmentFileName, "UTF-8");
            final String userAgent = request.getHeader("User-Agent");
            if (userAgent != null && userAgent.contains("Firefox")) {
                response.setHeader("Content-Disposition", "attachment; filename*=UTF-8''" + encodedfileName.replace("+", "%20"));
            } else {
                response.setHeader("Content-Disposition", "attachment; filename=\"" + encodedfileName.replaceAll("\\+", " ") + "\"; filename*=UTF-8''"
                        + encodedfileName.replace("+", "%20"));
            }
            final OutputStream out = response.getOutputStream();
            if (attachment == null) {
                response.setContentLength(0);
            } else {
                response.setContentLength(attachment.length);
                out.write(attachment);
            }
            out.close();
        } catch (final IOException e) {
            if (LOGGER.isLoggable(Level.SEVERE)) {
                LOGGER.log(Level.SEVERE, "Error while generating the response.", e);
            }
            throw new ServletException(e.getMessage(), e);
        }
    }

    protected byte[] getFileContent(final File file, final int fileLength, final String attachmentPath) throws ServletException {
        byte[] content;
        try {
            final InputStream fileInput = new FileInputStream(file);
            final byte[] fileContent = new byte[fileLength];
            try {
                int offset = 0;
                int length = fileLength;
                while (length > 0) {
                    final int read = fileInput.read(fileContent, offset, length);
                    if (read <= 0) {
                        break;
                    }
                    length -= read;
                    offset += read;
                }
                content = fileContent;
            } catch (final FileNotFoundException e) {
                final String errorMessage = "Error while getting the attachment. The file " + attachmentPath + " does not exist.";
                if (LOGGER.isLoggable(Level.SEVERE)) {
                    LOGGER.log(Level.SEVERE, errorMessage, e);
                }
                throw new ServletException(errorMessage, e);
            } finally {
                fileInput.close();
            }
        } catch (final IOException e) {
            final String errorMessage = "Error while reading attachment (file  : " + attachmentPath;
            if (LOGGER.isLoggable(Level.SEVERE)) {
                LOGGER.log(Level.SEVERE, errorMessage, e);
            }
            throw new ServletException(errorMessage, e);
        }
        return content;
    }
}
