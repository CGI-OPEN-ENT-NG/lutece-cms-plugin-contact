/*
 * Copyright (c) 2002-2021, City of Paris
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
import fr.paris.lutece.plugins.contact.business.ContactList;
import fr.paris.lutece.plugins.contact.business.ContactListHome;
import fr.paris.lutece.plugins.contact.service.ContactPlugin;
import fr.paris.lutece.portal.service.captcha.CaptchaSecurityService;
import fr.paris.lutece.portal.service.content.XPageAppService;
import fr.paris.lutece.portal.service.i18n.I18nService;
import fr.paris.lutece.portal.service.mail.MailService;
import fr.paris.lutece.portal.service.message.SiteMessage;
import fr.paris.lutece.portal.service.message.SiteMessageException;
import fr.paris.lutece.portal.service.message.SiteMessageService;
import fr.paris.lutece.portal.service.plugin.Plugin;
import fr.paris.lutece.portal.service.plugin.PluginService;
import fr.paris.lutece.portal.service.security.LuteceUser;
import fr.paris.lutece.portal.service.security.SecurityService;
import fr.paris.lutece.portal.service.security.UserNotSignedException;
import fr.paris.lutece.portal.service.template.AppTemplateService;
import fr.paris.lutece.portal.util.mvc.commons.annotations.Action;
import fr.paris.lutece.portal.util.mvc.commons.annotations.View;
import fr.paris.lutece.portal.util.mvc.utils.MVCUtils;
import fr.paris.lutece.portal.util.mvc.xpage.MVCApplication;
import fr.paris.lutece.portal.util.mvc.xpage.annotations.Controller;
import fr.paris.lutece.portal.web.xpages.XPage;
import fr.paris.lutece.util.ReferenceList;
import fr.paris.lutece.util.date.DateUtil;
import fr.paris.lutece.util.html.HtmlTemplate;
import fr.paris.lutece.util.string.StringUtil;
import fr.paris.lutece.util.url.UrlItem;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;

/**
 * This class manages Contact page.
 */
@Controller( xpageName = "contact", pageTitleI18nKey = "contact.pagePathLabel", pagePathI18nKey = "subscribe.pageTitle" )
public class ContactApp extends MVCApplication
{
    /** Serial id */
    private static final long serialVersionUID = 6553298772139973292L;

    ////////////////////////////////////////////////////////////////////////////
    // Constants
    private static final String TEMPLATE_XPAGE_CONTACT = "skin/plugins/contact/page_contact.html";
    private static final String TEMPLATE_XPAGE_LISTS = "skin/plugins/contact/page_lists.html";
    private static final String TEMPLATE_MESSAGE_CONTACT = "skin/plugins/contact/message_contact.html";
    private static final String MARK_CONTACTS_LIST = "contacts_list";
    private static final String MARK_DEFAULT_CONTACT = "default_contact";
    private static final String MARK_CONTACT_ALERT_MSG = "alert_msg";
    private static final String MARK_VISITOR_LASTNAME = "visitor_last_name";
    private static final String MARK_VISITOR_FIRSTNAME = "visitor_first_name";
    private static final String MARK_VISITOR_ADDRESS = "visitor_address";
    private static final String MARK_VISITOR_EMAIL = "visitor_email";
    private static final String MARK_OBJECT = "message_object";
    private static final String MARK_MESSAGE = "message";
    private static final String MARK_STYLE_LAST_NAME = "style_last_name";
    private static final String MARK_STYLE_FIRST_NAME = "style_first_name";
    private static final String MARK_STYLE_EMAIL = "style_email";
    private static final String MARK_STYLE_OBJECT = "style_object";
    private static final String MARK_STYLE_MESSAGE = "style_message";
    private static final String MARK_STYLE_CONTACT = "style_contact";
    private static final String MARK_PORTAL_URL = "portal_url";
    private static final String MARK_CONTACT_NAME = "contact_name";
    private static final String MARK_CURRENT_DATE = "current_date";
    private static final String MARK_CAPTCHA = "captcha";
    private static final String MARK_IS_ACTIVE_CAPTCHA = "is_active_captcha";
    private static final String MARK_IS_TOS_REQUIRED = "is_tos_required";
    private static final String MARK_TOS_MESSAGE = "tos_message";
    private static final String MARK_TOS_ACCEPTED = "accept_tos";
    // private static final String MARK_ROLE = "role";
    private static final String MARK_LIST_OF_LISTS = "list_of_lists";
    private static final String MARK_ID_CONTACT_LIST = "id_contact_list";
    private static final String MARK_MYLUTECE_USER = "mylutece_user";
    private static final String PARAMETER_PAGE = "page";
    private static final String PARAMETER_CONTACT = "contact";
    private static final String PARAMETER_VISITOR_LASTNAME = "visitor_last_name";
    private static final String PARAMETER_VISITOR_FIRSTNAME = "visitor_first_name";
    private static final String PARAMETER_VISITOR_ADDRESS = "visitor_address";
    private static final String PARAMETER_VISITOR_EMAIL = "visitor_email";
    private static final String PARAMETER_MESSAGE_OBJECT = "message_object";
    private static final String PARAMETER_MESSAGE = "message";
    private static final String PARAMETER_SEND = "send";
    private static final String PARAMETER_ID_CONTACT_LIST = "id_contact_list";
    private static final String PARAMETER_TOS_ACCEPTED = "accept_tos";
    private static final String PROPERTY_SENDING_OK = "contact.message_contact.sending.ok";
    private static final String PROPERTY_MANDATORY_FIELD_MISSING = "contact.message_contact.mandatory.field";
    private static final String PROPERTY_SENDING_NOK = "contact.message_contact.sending.nok";
    private static final String PROPERTY_RECIPIENT_MISSING = "contact.message_contact.recipient.missing";
    private static final String PROPERTY_ERROR_EMAIL = "contact.message_contact.error.email";
    private static final String PROPERTY_COMBO_CHOOSE = "contact.message_contact.comboChoose";
    private static final String PROPERTY_CAPTCHA_ERROR = "contact.message_contact.captchaError";
    private static final String PROPERTY_TOS_ERROR = "contact.message_contact.tosRequired";
    // private static final String PROPERTY_NO_ID_FOUND = "contact.message_contact.noIdFound";
    private static final String PROPERTY_LIST_NOT_EXISTS = "contact.message_contact.listNotExists";
    private static final String PROPERTY_NOT_AUTHORIZED = "contact.message_contact.notauthorized";
    private static final String PROPERTY_NO_LIST_VISIBLE = "contact.message_contact.noListVisible";
    private static final String JCAPTCHA_PLUGIN = "jcaptcha";
    private static final String EMPTY_STRING = "";
    private static final String ACTION_SEND_MESSAGE = "actionSendMessage";
    private static final String VIEW_CONTACT_LISTS = "viewContactLists";
    private static final String VIEW_CONTACT_PAGE = "viewContactPage";

    // Captcha
    private CaptchaSecurityService _captchaService;

    // private fields
    private Plugin _plugin;

    /**
     * Returns the content of the page
     *
     * @param request
     *            The http request
     * @param nMode
     *            The current mode
     * @param plugin
     *            The plugin object
     * @return The XPage
     * @throws fr.paris.lutece.portal.service.message.SiteMessageException
     *             Message displayed if an exception occurs
     * @throws UserNotSignedException
     *             if an authentication is required by a view
     */
    @Override
    public XPage getPage( HttpServletRequest request, int nMode, Plugin plugin ) throws SiteMessageException, UserNotSignedException
    {
        String strPluginName = request.getParameter( PARAMETER_PAGE );
        _plugin = PluginService.getPlugin( strPluginName );

        return super.getPage( request, nMode, plugin );
    }

    /**
     * Checks if the page is visible for the current user
     * 
     * @param request
     *            The HTTP request
     * @param strRole
     *            the role to check
     * @return true if the page could be shown to the user
     * @since v1.3.1
     */
    private boolean isVisible( HttpServletRequest request, String strRole )
    {
        if ( ( strRole == null ) || ( strRole.trim( ).equals( EMPTY_STRING ) ) )
        {
            return true;
        }

        if ( !strRole.equals( ContactList.ROLE_NONE ) && SecurityService.isAuthenticationEnable( ) )
        {
            return SecurityService.getInstance( ).isUserInRole( request, strRole );
        }

        return true;
    }

    /**
     * Return form
     * 
     * @param request
     *            the page request
     * @param strIdContactList
     *            the id of contact list
     * @param strSendMessage
     *            the send message
     * @return the corresponding form
     * @throws SiteMessageException
     *             occurs during treatment
     */
    @View( VIEW_CONTACT_PAGE )
    public XPage getForm( HttpServletRequest request ) throws SiteMessageException
    {
        String strPortalUrl = request.getRequestURI( );
        Map<String, Object> model = new HashMap<String, Object>( );

        model.put( MARK_PORTAL_URL, strPortalUrl );

        boolean bIsCaptchaEnabled = PluginService.isPluginEnable( JCAPTCHA_PLUGIN );
        model.put( MARK_IS_ACTIVE_CAPTCHA, bIsCaptchaEnabled );

        if ( bIsCaptchaEnabled )
        {
            _captchaService = new CaptchaSecurityService( );
            model.put( MARK_CAPTCHA, _captchaService.getHtmlCode( ) );
        }

        String strIdContactList = request.getParameter( PARAMETER_ID_CONTACT_LIST );
        String strSendMessage = request.getParameter( PARAMETER_SEND );
        String strVisitorLastName = ( request.getParameter( PARAMETER_VISITOR_LASTNAME ) != null ) ? request.getParameter( PARAMETER_VISITOR_LASTNAME ) : "";
        String strVisitorFirstName = ( request.getParameter( PARAMETER_VISITOR_FIRSTNAME ) != null ) ? request.getParameter( PARAMETER_VISITOR_FIRSTNAME ) : "";
        String strVisitorEmail = ( request.getParameter( PARAMETER_VISITOR_EMAIL ) != null ) ? request.getParameter( PARAMETER_VISITOR_EMAIL ) : "";
        String strVisitorAddress = ( request.getParameter( PARAMETER_VISITOR_ADDRESS ) != null ) ? request.getParameter( PARAMETER_VISITOR_ADDRESS ) : "";
        String strObject = ( request.getParameter( PARAMETER_MESSAGE_OBJECT ) != null ) ? request.getParameter( PARAMETER_MESSAGE_OBJECT ) : "";
        String strMessage = ( request.getParameter( PARAMETER_MESSAGE ) != null ) ? request.getParameter( PARAMETER_MESSAGE ) : "";
        String strContact = ( request.getParameter( PARAMETER_CONTACT ) != null ) ? request.getParameter( PARAMETER_CONTACT ) : "";

        if ( strSendMessage != null )
        {
            String strStyleLastName = strVisitorLastName.equals( "" ) ? "error" : "";
            String strStyleFirstName = strVisitorFirstName.equals( "" ) ? "error" : "";
            String strStyleEmail = ( strVisitorEmail.equals( "" ) || ( StringUtil.checkEmail( strVisitorEmail ) != true ) ) ? "error" : "";
            String strStyleObject = strObject.equals( "" ) ? "error" : "";
            String strStyleMessage = strMessage.equals( "" ) ? "error" : "";
            String strStyleContact = strContact.equals( "0" ) ? "error" : "";
            String strAlertMsg = "";

            if ( strSendMessage.equals( "done" ) )
            {
                UrlItem url = new UrlItem( strPortalUrl );
                url.addParameter( XPageAppService.PARAM_XPAGE_APP, ContactPlugin.PLUGIN_NAME );
                url.addParameter( MVCUtils.PARAMETER_VIEW, VIEW_CONTACT_PAGE );
                url.addParameter( PARAMETER_ID_CONTACT_LIST, strIdContactList );
                SiteMessageService.setMessage( request, PROPERTY_SENDING_OK, SiteMessage.TYPE_INFO, url.getUrl( ) );
            }

            else
                if ( strSendMessage.equals( "error_exception" ) )
                {
                    strAlertMsg = I18nService.getLocalizedString( PROPERTY_SENDING_NOK, request.getLocale( ) );
                }

                else
                    if ( strSendMessage.equals( "error_captcha" ) )
                    {
                        strAlertMsg = I18nService.getLocalizedString( PROPERTY_CAPTCHA_ERROR, request.getLocale( ) );
                    }

                    else
                        if ( strSendMessage.equals( "error_tos" ) )
                        {
                            strAlertMsg = I18nService.getLocalizedString( PROPERTY_TOS_ERROR, request.getLocale( ) );
                        }

                        else
                            if ( strSendMessage.equals( "error_field" ) )
                            {
                                strAlertMsg = I18nService.getLocalizedString( PROPERTY_MANDATORY_FIELD_MISSING, request.getLocale( ) );
                            }

                            else
                                if ( strSendMessage.equals( "error_recipient" ) )
                                {
                                    strAlertMsg = I18nService.getLocalizedString( PROPERTY_RECIPIENT_MISSING, request.getLocale( ) );
                                }
                                else
                                    if ( strSendMessage.equals( "error_email" ) )
                                    {
                                        strAlertMsg = I18nService.getLocalizedString( PROPERTY_ERROR_EMAIL, request.getLocale( ) );
                                    }

            model.put( MARK_CONTACT_ALERT_MSG, strAlertMsg );
            model.put( MARK_STYLE_LAST_NAME, strStyleLastName );
            model.put( MARK_STYLE_FIRST_NAME, strStyleFirstName );
            model.put( MARK_STYLE_OBJECT, strStyleObject );
            model.put( MARK_STYLE_EMAIL, strStyleEmail );
            model.put( MARK_STYLE_MESSAGE, strStyleMessage );
            model.put( MARK_STYLE_CONTACT, strStyleContact );
        }

        int nIdContactList = Integer.parseInt( strIdContactList );
        ContactList contactList = ContactListHome.findByPrimaryKey( nIdContactList, _plugin );

        if ( !ContactListHome.listExists( contactList.getId( ), _plugin ) )
        {
            SiteMessageService.setMessage( request, PROPERTY_LIST_NOT_EXISTS, SiteMessage.TYPE_ERROR );
        }

        if ( !isVisible( request, contactList.getRole( ) ) )
        {
            SiteMessageService.setMessage( request, PROPERTY_NOT_AUTHORIZED, SiteMessage.TYPE_STOP );
        }

        String strComboItem = I18nService.getLocalizedString( PROPERTY_COMBO_CHOOSE, request.getLocale( ) );

        // Contacts Combo
        ReferenceList listContact = ContactHome.getContactsByListWithString( contactList.getId( ), strComboItem, _plugin );

        if ( SecurityService.isAuthenticationEnable( ) )
        {
            LuteceUser user = SecurityService.getInstance( ).getRegisteredUser( request );

            if ( user != null )
            {
                model.put( MARK_MYLUTECE_USER, user );
            }
        }

        model.put( MARK_CONTACTS_LIST, listContact );
        model.put( MARK_VISITOR_LASTNAME, strVisitorLastName );
        model.put( MARK_VISITOR_FIRSTNAME, strVisitorFirstName );
        model.put( MARK_VISITOR_EMAIL, strVisitorEmail );
        model.put( MARK_VISITOR_ADDRESS, strVisitorAddress );
        model.put( MARK_OBJECT, strObject );
        model.put( MARK_MESSAGE, strMessage );
        model.put( MARK_ID_CONTACT_LIST, nIdContactList );

        boolean bIsTosRequired = contactList.getTos( );
        model.put( MARK_IS_TOS_REQUIRED, bIsTosRequired );

        if ( bIsTosRequired )
        {
            String strTosMessage = contactList.getTosMessage( );
            model.put( MARK_TOS_MESSAGE, strTosMessage );

            boolean bTosAccepted = request.getParameter( PARAMETER_TOS_ACCEPTED ) != null;
            model.put( MARK_TOS_ACCEPTED, bTosAccepted );
        }

        model.put( MARK_DEFAULT_CONTACT, ( ( strContact == null ) || ( strContact.equals( "" ) ) ) ? "0" : strContact );

        return getXPage( TEMPLATE_XPAGE_CONTACT, request.getLocale( ), model );
    }

    /**
     * Get lists
     * 
     * @param request
     *            the page request
     * @return the lists
     * @throws SiteMessageException
     *             occurs during treatment
     */
    @View( value = VIEW_CONTACT_LISTS, defaultView = true )
    public XPage getLists( HttpServletRequest request ) throws SiteMessageException
    {
        Map<String, Object> model = new HashMap<String, Object>( );
        Collection<ContactList> listOfLists = ContactListHome.findAll( _plugin );

        Collection<ContactList> visibleList = new ArrayList<ContactList>( ); // filter the list of lists by role

        for ( ContactList currentList : listOfLists )
        {
            if ( isVisible( request, currentList.getRole( ) ) )
            {
                visibleList.add( currentList );
            }
        }

        if ( visibleList.size( ) == 0 )
        {
            SiteMessageService.setMessage( request, PROPERTY_NO_LIST_VISIBLE, SiteMessage.TYPE_WARNING );
        }
        else
            if ( visibleList.size( ) == 1 )
            {
                String strContactListId = StringUtils.EMPTY;

                for ( ContactList onlyList : visibleList )
                {
                    strContactListId = Integer.toString( onlyList.getId( ) );
                }

                Map<String, String> mapParameters = new HashMap<String, String>( );
                mapParameters.put( PARAMETER_ID_CONTACT_LIST, strContactListId );

                return redirect( request, VIEW_CONTACT_PAGE, mapParameters );
            }

        model.put( MARK_LIST_OF_LISTS, visibleList );

        return getXPage( TEMPLATE_XPAGE_LISTS, request.getLocale( ), model );
    }

    /**
     * This method tests the parameters stored in the request and send the message if they are corrects. Otherwise, it displays an error message.
     * 
     * @return The result of the process of the sending message.
     * @param request
     *            The http request
     * @throws fr.paris.lutece.portal.service.message.SiteMessageException
     *             Message displayed if an exception occures
     */
    @Action( ACTION_SEND_MESSAGE )
    public XPage doSendMessage( HttpServletRequest request ) throws SiteMessageException
    {
        String strIdContactList = request.getParameter( PARAMETER_ID_CONTACT_LIST );
        String strVisitorLastName = ( request.getParameter( PARAMETER_VISITOR_LASTNAME ) == null ) ? "" : this.encodingWithUTF16(request.getParameter( PARAMETER_VISITOR_LASTNAME ));
        String strVisitorFirstName = ( request.getParameter( PARAMETER_VISITOR_FIRSTNAME ) == null ) ? "" : this.encodingWithUTF16(request.getParameter( PARAMETER_VISITOR_FIRSTNAME ));
        String strVisitorAddress = ( request.getParameter( PARAMETER_VISITOR_ADDRESS ) == null ) ? "" : this.encodingWithUTF16(request.getParameter( PARAMETER_VISITOR_ADDRESS ));
        String strVisitorEmail = ( request.getParameter( PARAMETER_VISITOR_EMAIL ) == null ) ? "" : this.encodingWithUTF16(request.getParameter( PARAMETER_VISITOR_EMAIL ));
        String strObject = ( request.getParameter( PARAMETER_MESSAGE_OBJECT ) == null ) ? "" : this.encodingWithUTF16(request.getParameter( PARAMETER_MESSAGE_OBJECT ));
        String strMessage = ( request.getParameter( PARAMETER_MESSAGE ) == null ) ? "" : this.encodingWithUTF16(request.getParameter( PARAMETER_MESSAGE ));
        String strDateOfDay = DateUtil.getCurrentDateString( request.getLocale( ) );
        String strContact = this.encodingWithUTF16(request.getParameter( PARAMETER_CONTACT ));
        int nContact = ( strContact == null ) ? 0 : Integer.parseInt( strContact );
        int nIdContactList = Integer.parseInt( strIdContactList );
        boolean bTosAccepted = request.getParameter( PARAMETER_TOS_ACCEPTED ) != null;

        Map<String, String> mapParamError = new HashMap<String, String>( );
        mapParamError.put( PARAMETER_ID_CONTACT_LIST, strIdContactList );
        mapParamError.put( PARAMETER_VISITOR_LASTNAME, strVisitorLastName );
        mapParamError.put( PARAMETER_VISITOR_FIRSTNAME, strVisitorFirstName );
        mapParamError.put( PARAMETER_VISITOR_ADDRESS, strVisitorAddress );
        mapParamError.put( PARAMETER_VISITOR_EMAIL, strVisitorEmail );
        mapParamError.put( PARAMETER_CONTACT, strContact );
        mapParamError.put( PARAMETER_MESSAGE_OBJECT, strObject );
        mapParamError.put( PARAMETER_MESSAGE, strMessage );
        if ( bTosAccepted )
        {
            mapParamError.put( PARAMETER_TOS_ACCEPTED, "1" );
        }

        // test the captcha
        if ( PluginService.isPluginEnable( JCAPTCHA_PLUGIN ) )
        {
            _captchaService = new CaptchaSecurityService( );

            if ( !_captchaService.validate( request ) )
            {
                mapParamError.put( PARAMETER_SEND, "error_captcha" );

                return redirect( request, VIEW_CONTACT_PAGE, mapParamError );
            }
        }

        // test the selection of the contact
        if ( nContact == 0 )
        {
            mapParamError.put( PARAMETER_SEND, "error_recipient" );

            return redirect( request, VIEW_CONTACT_PAGE, mapParamError );
        }

        Contact contact = ContactHome.findByPrimaryKey( nContact, _plugin );
        String strEmailContact = contact.getEmail( );
        String strContactName = contact.getName( );

        // tests the length of the message ( 1000 characters maximums )
        if ( strMessage.length( ) > 1000 )
        {
            strMessage = strMessage.substring( 0, 1000 );
        }

        // Mandatory fields
        if ( strVisitorLastName.equals( "" ) || strVisitorFirstName.equals( "" ) || strVisitorEmail.equals( "" ) || strContact.equals( "" )
                || strObject.equals( "" ) || strMessage.equals( "" ) )
        {
            mapParamError.put( PARAMETER_SEND, "error_field" );

            return redirect( request, VIEW_CONTACT_PAGE, mapParamError );
        }

        // test the email of the visitor
        // Checking of the presence of the email address and of its format (@ caracter in the address).
        if ( StringUtil.checkEmail( strVisitorEmail ) != true )
        {
            mapParamError.put( PARAMETER_SEND, "error_email" );

            return redirect( request, VIEW_CONTACT_PAGE, mapParamError );
        }

        ContactList contactlist = ContactListHome.findByPrimaryKey( nIdContactList, _plugin );
        boolean bIsTosRequired = contactlist.getTos( );

        // test the checking of the terms of service
        if ( bIsTosRequired )
        {
            if ( !bTosAccepted )
            {
                mapParamError.put( PARAMETER_SEND, "error_tos" );

                return redirect( request, VIEW_CONTACT_PAGE, mapParamError );
            }
        }

        Map<String, String> model = new HashMap<String, String>( );
        model.put( MARK_VISITOR_LASTNAME, strVisitorLastName );
        model.put( MARK_VISITOR_FIRSTNAME, strVisitorFirstName );
        model.put( MARK_VISITOR_ADDRESS, strVisitorAddress );
        model.put( MARK_VISITOR_EMAIL, strVisitorEmail );
        model.put( MARK_CONTACT_NAME, strContactName );
        model.put( MARK_MESSAGE, strMessage );
        model.put( MARK_CURRENT_DATE, strDateOfDay );

        HtmlTemplate template = AppTemplateService.getTemplate( TEMPLATE_MESSAGE_CONTACT, request.getLocale( ), model );

        String strMessageText = template.getHtml( );

        String strNoReplyEmail = MailService.getNoReplyEmail();

        MailService.sendMailHtml(strEmailContact, strVisitorLastName, strNoReplyEmail, strObject, strMessageText );
        ContactHome.updateHits( nContact, nIdContactList, _plugin );

        Map<String, String> mapParamSuccess = new HashMap<String, String>( );
        mapParamSuccess.put( PARAMETER_ID_CONTACT_LIST, strIdContactList );
        mapParamSuccess.put( PARAMETER_SEND, "done" );

        return redirect( request, VIEW_CONTACT_PAGE, mapParamSuccess );
    }

    /**
     * This method allows to encode a String to UTF-8 in Java.
     *
     * @return The string encode.
     * @param rawString The raw string
     */
    private String encodingWithUTF16(String rawString){
        byte[] bytes = rawString.getBytes(StandardCharsets.UTF_16);
        return new String(bytes, StandardCharsets.UTF_16);
    }
}
