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

import fr.paris.lutece.plugins.contact.web.ContactJspBean;
import fr.paris.lutece.portal.business.rbac.RBACRole;
import fr.paris.lutece.portal.business.user.AdminUser;
import fr.paris.lutece.portal.business.user.AdminUserHome;
import fr.paris.lutece.portal.service.admin.AccessDeniedException;
import fr.paris.lutece.portal.service.plugin.Plugin;
import fr.paris.lutece.portal.service.plugin.PluginService;
import fr.paris.lutece.portal.util.mvc.utils.MVCUtils;
import fr.paris.lutece.test.LuteceTestCase;
import fr.paris.lutece.test.Utils;

import java.util.HashMap;
import java.util.Locale;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.mock.web.MockHttpServletRequest;

public class ContactJspBeanTest extends LuteceTestCase
{
    // parameters
    private static final String PARAMETER_CONTACT_ID = "id_contact";
    private static final String PARAMETER_CONTACT_NAME = "contact_name";
    private static final String PARAMETER_CONTACT_EMAIL = "contact_email";
    private static final String PARAMETER_ID_CONTACT_LIST = "id_contact_list";
    private static final String PARAMETER_CONTACT_WORKGROUP = "workgroup";
    private static final String ACTION_CREATE_CONTACT = "actionCreateContact";
    private static final String ACTION_MODIFY_CONTACT = "actionModifyContact";
    private static final String VIEW_CONFIRM_REMOVE_CONTACT = "viewConfirmRemoveContact";

    /**
     * Test of getCreateContact method, of class fr.paris.lutece.plugins.contact.web.contactJspBean.
     */
    public void testGetCreateContact( ) throws AccessDeniedException
    {
        System.out.println( "getCreateContact" );

        MockHttpServletRequest request = new MockHttpServletRequest( );
        AdminUser user = new AdminUser( );
        user.setRoles( new HashMap<String, RBACRole>( ) );
        user.setLocale( Locale.getDefault( ) );
        Utils.registerAdminUserWithRigth( request, user, ContactJspBean.RIGHT_MANAGE_CONTACT );

        ContactJspBean instance = new ContactJspBean( );

        instance.init( request, ContactJspBean.RIGHT_MANAGE_CONTACT );
        String result = instance.getCreateContact( request );
    }

    /**
     * Test of getModifyContact method, of class fr.paris.lutece.plugins.contact.web.contactJspBean.
     */
    public void testDoCreateContact( ) throws AccessDeniedException
    {
        System.out.println( "doCreateContact" );

        MockHttpServletRequest request = new MockHttpServletRequest( );
        AdminUser user = new AdminUser( );
        user.setRoles( new HashMap<String, RBACRole>( ) );
        user.setLocale( Locale.getDefault( ) );
        Utils.registerAdminUserWithRigth( request, user, ContactJspBean.RIGHT_MANAGE_CONTACT );
        request.addParameter( MVCUtils.PARAMETER_ACTION, ACTION_CREATE_CONTACT );
        request.addParameter( PARAMETER_CONTACT_NAME, "test name" );
        request.addParameter( PARAMETER_CONTACT_EMAIL, "test email" );
        request.addParameter( PARAMETER_CONTACT_WORKGROUP, "all" );

        MockHttpServletResponse response = new MockHttpServletResponse( );

        ContactJspBean instance = new ContactJspBean( );

        String result = instance.processController( request, response );
    }

    /**
     * Test of getModifyContact method, of class fr.paris.lutece.plugins.contact.web.contactJspBean.
     */
    public void testGetModifyContact( ) throws AccessDeniedException
    {
        System.out.println( "getModifyContact" );

        MockHttpServletRequest request = new MockHttpServletRequest( );
        AdminUser user = new AdminUser( );
        user.setRoles( new HashMap<String, RBACRole>( ) );
        user.setLocale( Locale.getDefault( ) );
        Utils.registerAdminUserWithRigth( request, user, ContactJspBean.RIGHT_MANAGE_CONTACT );
        request.addParameter( PARAMETER_CONTACT_ID, "1" );

        ContactJspBean instance = new ContactJspBean( );

        instance.init( request, ContactJspBean.RIGHT_MANAGE_CONTACT );
        String result = instance.getModifyContact( request );
    }

    /**
     * Test of getConfirmRemoveContact method, of class fr.paris.lutece.plugins.contact.web.contactJspBean.
     */
    public void testGetConfirmRemoveContact( ) throws AccessDeniedException
    {
        System.out.println( "getConfirmRemoveContact" );

        MockHttpServletRequest request = new MockHttpServletRequest( );
        AdminUser user = new AdminUser( );
        user.setRoles( new HashMap<String, RBACRole>( ) );
        user.setLocale( Locale.getDefault( ) );
        Utils.registerAdminUserWithRigth( request, user, ContactJspBean.RIGHT_MANAGE_CONTACT );
        request.addParameter( MVCUtils.PARAMETER_VIEW, VIEW_CONFIRM_REMOVE_CONTACT );
        request.addParameter( PARAMETER_CONTACT_ID, "1" );

        MockHttpServletResponse response = new MockHttpServletResponse( );

        ContactJspBean instance = new ContactJspBean( );

        String result = instance.processController( request, response );
    }

    /**
     * Test of getManageContacts method, of class fr.paris.lutece.plugins.contact.web.contactJspBean.
     */
    public void testGetManageContacts( ) throws AccessDeniedException
    {
        System.out.println( "getManageContacts" );

        MockHttpServletRequest request = new MockHttpServletRequest( );
        AdminUser user = AdminUserHome.findUserByLogin( "admin" );
        user.setRoles( AdminUserHome.getRolesListForUser( user.getUserId( ) ) );
        Utils.registerAdminUserWithRigth( request, user, ContactJspBean.RIGHT_MANAGE_CONTACT );

        ContactJspBean instance = new ContactJspBean( );
        instance.init( request, ContactJspBean.RIGHT_MANAGE_CONTACT );

        String result = instance.getManageContacts( request );
    }

    /**
     * Test of testGetManageContactsHome method, of class fr.paris.lutece.plugins.contact.web.contactJspBean.
     */
    public void testGetManageContactsHome( ) throws AccessDeniedException
    {
        System.out.println( "getManageContactsHome" );

        MockHttpServletRequest request = new MockHttpServletRequest( );
        AdminUser user = AdminUserHome.findUserByLogin( "admin" );
        user.setRoles( AdminUserHome.getRolesListForUser( user.getUserId( ) ) );
        Utils.registerAdminUserWithRigth( request, user, ContactJspBean.RIGHT_MANAGE_CONTACT );

        ContactJspBean instance = new ContactJspBean( );
        instance.init( request, ContactJspBean.RIGHT_MANAGE_CONTACT );

        String result = instance.getManageContactsHome( request );
    }

    /**
     * Test of getModifyContact method, of class fr.paris.lutece.plugins.contact.web.contactJspBean.
     */
    public void testDoModifyContact( ) throws AccessDeniedException
    {
        System.out.println( "doModifyContact" );

        MockHttpServletRequest request = new MockHttpServletRequest( );
        AdminUser user = new AdminUser( );
        user.setRoles( new HashMap<String, RBACRole>( ) );
        user.setLocale( Locale.getDefault( ) );
        Utils.registerAdminUserWithRigth( request, user, ContactJspBean.RIGHT_MANAGE_CONTACT );
        request.addParameter( MVCUtils.PARAMETER_ACTION, ACTION_MODIFY_CONTACT );
        request.addParameter( PARAMETER_CONTACT_ID, "1" );
        request.addParameter( PARAMETER_CONTACT_NAME, "test name 2" );
        request.addParameter( PARAMETER_CONTACT_EMAIL, "test email 2" );
        request.addParameter( PARAMETER_CONTACT_WORKGROUP, "all" );

        MockHttpServletResponse response = new MockHttpServletResponse( );

        ContactJspBean instance = new ContactJspBean( );

        String result = instance.processController( request, response );
    }
}