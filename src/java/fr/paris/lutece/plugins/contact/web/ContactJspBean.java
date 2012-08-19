/*
 * Copyright (c) 2002-2012, Mairie de Paris
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 *  1. Redistributions of source code must retain the above copyright notice
 *     and the following disclaimer.
 *
 *  2. Redistributions in binary form must reproduce the above copyright notice
 *     and the following disclaimer in the documentation and/or other materials
 *     provided with the distribution.
 *
 *  3. Neither the name of 'Mairie de Paris' nor 'Lutece' nor the names of its
 *     contributors may be used to endorse or promote products derived from
 *     this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDERS OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 *
 * License 1.0
 */
package fr.paris.lutece.plugins.contact.web;

import fr.paris.lutece.plugins.contact.business.Contact;
import fr.paris.lutece.plugins.contact.business.ContactHome;
import fr.paris.lutece.plugins.contact.business.ContactListHome;
import fr.paris.lutece.portal.service.message.AdminMessage;
import fr.paris.lutece.portal.service.message.AdminMessageService;
import fr.paris.lutece.portal.service.template.AppTemplateService;
import fr.paris.lutece.portal.service.util.AppPropertiesService;
import fr.paris.lutece.portal.service.workgroup.AdminWorkgroupService;
import fr.paris.lutece.portal.web.admin.PluginAdminPageJspBean;
import fr.paris.lutece.portal.web.constants.Messages;
import fr.paris.lutece.portal.web.util.LocalizedPaginator;
import fr.paris.lutece.util.ReferenceList;
import fr.paris.lutece.util.html.HtmlTemplate;
import fr.paris.lutece.util.html.Paginator;
import fr.paris.lutece.util.string.StringUtil;
import fr.paris.lutece.util.url.UrlItem;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;


/**
 * This class provides the user interface to manage contact features ( manage, create, modify, remove, change order of
 * contact )
 */
public class ContactJspBean extends PluginAdminPageJspBean
{
    ////////////////////////////////////////////////////////////////////////////
    // Constants

    // Right
    public static final String RIGHT_MANAGE_CONTACT = "CONTACT_MANAGEMENT";

    // parameters
    private static final String PARAMETER_CONTACT_ID = "id_contact";
    private static final String PARAMETER_CONTACT_NAME = "contact_name";
    private static final String PARAMETER_CONTACT_EMAIL = "contact_email";
    private static final String PARAMETER_ID_CONTACT_LIST = "id_contact_list";
    private static final String PARAMETER_CONTACT_WORKGROUP = "workgroup";
    private static final String PARAMETER_PAGE_INDEX = "page_index";

    // templates
    private static final String TEMPLATE_CONTACTS = "/admin/plugins/contact/manage_contacts.html";
    private static final String TEMPLATE_CREATE_CONTACT = "/admin/plugins/contact/create_contact.html";
    private static final String TEMPLATE_MODIFY_CONTACT = "/admin/plugins/contact/modify_contact.html";
    private static final String TEMPLATE_HOME = "/admin/plugins/contact/manage_contacts_home.html";

    // properties for page titles
    private static final String PROPERTY_PAGE_TITLE_CONTACTS = "contact.manage_contacts.pageTitle";
    private static final String PROPERTY_PAGE_TITLE_MODIFY = "contact.modify_contact.pageTitle";
    private static final String PROPERTY_PAGE_TITLE_CREATE = "contact.create_contact.pageTitle";
    private static final String PROPERTY_PAGE_TITLE_FEATURES = "contact.manage_features.pageTitle";

    // Markers
    private static final String MARK_CONTACT_LIST = "contact_list";
    private static final String MARK_WORKGROUPS_LIST = "workgroups_list";
    private static final String MARK_CONTACT = "contact";
    private static final String MARK_PAGINATOR = "paginator";
    private static final String MARK_NB_ITEMS_PER_PAGE = "nb_items_per_page";

    // Jsp Definition
    private static final String JSP_DO_REMOVE_CONTACT = "jsp/admin/plugins/contact/DoRemoveContact.jsp";
    private static final String JSP_MANAGE_CONTACTS = "jsp/admin/plugins/contact/ManageContacts.jsp";
    private static final String JSP_REDIRECT_TO_MANAGE_CONTACTS = "ManageContacts.jsp";

    // Properties
    private static final String PROPERTY_DEFAULT_LIST_CONTACT_PER_PAGE = "contact.listContacts.itemsPerPage";

    // Messages
    private static final String MESSAGE_CONFIRM_REMOVE_CONTACT = "contact.message.confirmRemoveContact";
    private static final String MESSAGE_EMAIL_NOT_VALID = "contact.message.emailNotValid";
    private static final String MESSAGE_CONTACT_STILL_ASSIGNED = "contact.message.ContactStillAssigned";

    //Variables
    private int _nDefaultItemsPerPage;
    private String _strCurrentPageIndex;
    private int _nItemsPerPage;

    /**
     * returns the template, with the 2 features: contacts, and contactList
     * @param request the HttpRequest
     * @return template model
     */
    public String getManageContactsHome( HttpServletRequest request )
    {
        setPageTitleProperty( PROPERTY_PAGE_TITLE_FEATURES );

        HashMap rootModel = new HashMap(  );
        HtmlTemplate templateList = AppTemplateService.getTemplate( TEMPLATE_HOME, getLocale(  ), rootModel );

        return getAdminPage( templateList.getHtml(  ) );
    }

    /**
     * Returns the list of people to contact by email
     *
     * @param request The Http request
     * @return the contacts list
     */
    public String getManageContacts( HttpServletRequest request )
    {
        setPageTitleProperty( PROPERTY_PAGE_TITLE_CONTACTS );

        _strCurrentPageIndex = Paginator.getPageIndex( request, Paginator.PARAMETER_PAGE_INDEX, _strCurrentPageIndex );
        _nDefaultItemsPerPage = AppPropertiesService.getPropertyInt( PROPERTY_DEFAULT_LIST_CONTACT_PER_PAGE, 50 );
        _nItemsPerPage = Paginator.getItemsPerPage( request, Paginator.PARAMETER_ITEMS_PER_PAGE, _nItemsPerPage,
                _nDefaultItemsPerPage );

        Collection<Contact> listContacts = ContactHome.findAll( getPlugin(  ) );
        listContacts = AdminWorkgroupService.getAuthorizedCollection( listContacts, getUser(  ) );

        LocalizedPaginator paginator = new LocalizedPaginator( (List<Contact>) listContacts, _nItemsPerPage, getUrlPage(  ),
                PARAMETER_PAGE_INDEX, _strCurrentPageIndex ,getLocale() );

        Map<String, Object> model = new HashMap<String, Object>(  );

        model.put( MARK_NB_ITEMS_PER_PAGE, "" + _nItemsPerPage );
        model.put( MARK_PAGINATOR, paginator );
        model.put( MARK_CONTACT_LIST, paginator.getPageItems(  ) );

        HtmlTemplate templateList = AppTemplateService.getTemplate( TEMPLATE_CONTACTS, getLocale(  ), model );

        return getAdminPage( templateList.getHtml(  ) );
    }

    /**
     * Returns the form to create a contact
     *
     * @param request The Http request
     * @return the html code of the contact form
     */
    public String getCreateContact( HttpServletRequest request )
    {
        setPageTitleProperty( PROPERTY_PAGE_TITLE_CREATE );

        Map<String, ReferenceList> model = new HashMap<String, ReferenceList>(  );
        ReferenceList workgroupsList = AdminWorkgroupService.getUserWorkgroups( getUser(  ), getLocale(  ) );
        model.put( MARK_WORKGROUPS_LIST, workgroupsList );

        HtmlTemplate template = AppTemplateService.getTemplate( TEMPLATE_CREATE_CONTACT, getLocale(  ), model );

        return getAdminPage( template.getHtml(  ) );
    }

    /**
     * Process the data capture form of a new contact
     *
     * @param request The Http Request
     * @return The Jsp URL of the process result
     */
    public String doCreateContact( HttpServletRequest request )
    {
        Contact contact = new Contact(  );
        contact.setName( request.getParameter( PARAMETER_CONTACT_NAME ) );
        contact.setEmail( request.getParameter( PARAMETER_CONTACT_EMAIL ) );
        contact.setWorkgroup( request.getParameter( PARAMETER_CONTACT_WORKGROUP ) );

        // Mandatory fields
        if ( request.getParameter( PARAMETER_CONTACT_NAME ).equals( "" ) ||
                request.getParameter( PARAMETER_CONTACT_EMAIL ).equals( "" ) )
        {
            return AdminMessageService.getMessageUrl( request, Messages.MANDATORY_FIELDS, AdminMessage.TYPE_STOP );
        }

        else if ( !StringUtil.checkEmail( request.getParameter( PARAMETER_CONTACT_EMAIL ) ) )
        {
            return AdminMessageService.getMessageUrl( request, MESSAGE_EMAIL_NOT_VALID, AdminMessage.TYPE_STOP );
        }

        ContactHome.create( contact, getPlugin(  ) );

        // if the operation occurred well, redirects towards the list of the Contacts
        return JSP_REDIRECT_TO_MANAGE_CONTACTS;
    }

    /**
     * Returns the form to update info about a contact
     *
     * @param request The Http request
     * @return The HTML form to update info
     */
    public String getModifyContact( HttpServletRequest request )
    {
        setPageTitleProperty( PROPERTY_PAGE_TITLE_MODIFY );

        int nId = Integer.parseInt( request.getParameter( PARAMETER_CONTACT_ID ) );
        Contact contact = ContactHome.findByPrimaryKey( nId, getPlugin(  ) );

        Map<String, Object> model = new HashMap<String, Object>(  );
        ReferenceList workgroupsList = AdminWorkgroupService.getUserWorkgroups( getUser(  ), getLocale(  ) );
        model.put( MARK_WORKGROUPS_LIST, workgroupsList );
        model.put( MARK_CONTACT, contact );

        HtmlTemplate template = AppTemplateService.getTemplate( TEMPLATE_MODIFY_CONTACT, getLocale(  ), model );

        return getAdminPage( template.getHtml(  ) );
    }

    /**
     * Process the change form of a contact
     *
     * @param request The Http request
     * @return The Jsp URL of the process result
     */
    public String doModifyContact( HttpServletRequest request )
    {
        // Mandatory fields
        if ( request.getParameter( PARAMETER_CONTACT_NAME ).equals( "" ) ||
                request.getParameter( PARAMETER_CONTACT_EMAIL ).equals( "" ) )
        {
            return AdminMessageService.getMessageUrl( request, Messages.MANDATORY_FIELDS, AdminMessage.TYPE_STOP );
        }
        else if ( !StringUtil.checkEmail( request.getParameter( PARAMETER_CONTACT_EMAIL ) ) )
        {
            return AdminMessageService.getMessageUrl( request, MESSAGE_EMAIL_NOT_VALID, AdminMessage.TYPE_STOP );
        }

        int nId = Integer.parseInt( request.getParameter( PARAMETER_CONTACT_ID ) );
        Contact contact = ContactHome.findByPrimaryKey( nId, getPlugin(  ) );
        contact.setName( request.getParameter( PARAMETER_CONTACT_NAME ) );
        contact.setEmail( request.getParameter( PARAMETER_CONTACT_EMAIL ) );
        contact.setWorkgroup( request.getParameter( PARAMETER_CONTACT_WORKGROUP ) );
        ContactHome.update( contact, getPlugin(  ) );

        // if the operation occurred well, redirects towards the list of the Contacts
        return JSP_REDIRECT_TO_MANAGE_CONTACTS;
    }

    /**
     * Manages the removal form of a contact whose identifier is in the http request
     *
     * @param request The Http request
     * @return the html code to confirm
     */
    public String getConfirmRemoveContact( HttpServletRequest request )
    {
        int nIdContact = Integer.parseInt( request.getParameter( PARAMETER_CONTACT_ID ) );

        if ( ContactListHome.countListsForContact( nIdContact, getPlugin(  ) ) > 0 )
        {
            UrlItem url = new UrlItem( JSP_MANAGE_CONTACTS );

            return AdminMessageService.getMessageUrl( request, MESSAGE_CONTACT_STILL_ASSIGNED, url.getUrl(  ),
                AdminMessage.TYPE_STOP );
        }
        else
        {
            UrlItem url = new UrlItem( JSP_DO_REMOVE_CONTACT );
            url.addParameter( PARAMETER_CONTACT_ID, nIdContact );
            url.addParameter( PARAMETER_ID_CONTACT_LIST, request.getParameter( PARAMETER_ID_CONTACT_LIST ) );

            return AdminMessageService.getMessageUrl( request, MESSAGE_CONFIRM_REMOVE_CONTACT, url.getUrl(  ),
                AdminMessage.TYPE_CONFIRMATION );
        }
    }

    /**
     * Treats the removal form of a contact
     *
     * @param request The Http request
     * @return the jsp URL to display the form to manage contacts
     */
    public String doRemoveContact( HttpServletRequest request )
    {
        int nIdContact = Integer.parseInt( request.getParameter( PARAMETER_CONTACT_ID ) );

        Contact contact = ContactHome.findByPrimaryKey( nIdContact, getPlugin(  ) );

        ContactListHome.unassignListsForContact( contact.getId(  ), getPlugin(  ) );
        ContactHome.remove( contact, getPlugin(  ) );
        ContactListHome.unassignListsForContact( nIdContact, getPlugin(  ) );

        // if the operation occurred well, redirects towards the list of the Contacts
        return JSP_REDIRECT_TO_MANAGE_CONTACTS;
    }

    /**
     * Return UrlPage Url
     * @return url
     */
    private String getUrlPage(  )
    {
        UrlItem url = new UrlItem( JSP_MANAGE_CONTACTS );

        return url.getUrl(  );
    }
}
