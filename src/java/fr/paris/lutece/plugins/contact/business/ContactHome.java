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
package fr.paris.lutece.plugins.contact.business;

import fr.paris.lutece.portal.service.plugin.Plugin;
import fr.paris.lutece.portal.service.spring.SpringContextService;
import fr.paris.lutece.util.ReferenceList;

import java.util.Collection;

/**
 * This class provides instances management methods (create, find, ...) for Contact objects
 */
public final class ContactHome
{
    // Static variable pointed at the DAO instance
    private static IContactDAO _dao = (IContactDAO) SpringContextService.getPluginBean( "contact", "contactDAO" );

    /**
     * Private constructor - this class need not be instantiated
     */
    private ContactHome( )
    {
    }

    /**
     * Creation of an instance of contact
     *
     * @param contact
     *            The instance of the contact which contains the informations to store
     * @param plugin
     *            The Plugin object
     * @return The instance of contact which has been created with its primary key.
     */
    public static Contact create( Contact contact, Plugin plugin )
    {
        _dao.insert( contact, plugin );

        return contact;
    }

    /**
     * Update of the contact which is specified in parameter
     *
     * @param contact
     *            The instance of the contact which contains the data to store
     * @param plugin
     *            The Plugin object
     * @return The instance of the contact which has been updated
     */
    public static Contact update( Contact contact, Plugin plugin )
    {
        _dao.store( contact, plugin );

        return contact;
    }

    /**
     * Remove the Contact whose identifier is specified in parameter
     *
     * @param contact
     *            The Contact object to remove
     * @param plugin
     *            The Plugin object
     */
    public static void remove( Contact contact, Plugin plugin )
    {
        _dao.delete( contact, plugin );
    }

    ///////////////////////////////////////////////////////////////////////////
    // Finders

    /**
     * Returns an instance of a contact whose identifier is specified in parameter
     *
     * @param nKey
     *            The Primary key of the contact
     * @param plugin
     *            The Plugin object
     * @return An instance of contact
     */
    public static Contact findByPrimaryKey( int nKey, Plugin plugin )
    {
        return _dao.load( nKey, plugin );
    }

    /**
     * Returns a collection of contacts objects
     * 
     * @param plugin
     *            The Plugin object
     * @return A collection of contacts
     */
    public static Collection<Contact> findAll( Plugin plugin )
    {
        return _dao.selectAll( plugin );
    }

    ////////////////////////////////////////////////////////////////////////////
    // References List management

    /**
     * Returns a reference list which contains all the contacts and the string "choose your contact", for the xpage
     * 
     * @return a reference list
     * @param nIdContactList
     *            The id of contactList
     * @param strComboItem
     *            The string to display at the top of combo
     * @param plugin
     *            The Plugin object
     */
    public static ReferenceList getContactsByListWithString( int nIdContactList, String strComboItem, Plugin plugin )
    {
        return _dao.selectContactsByListWithString( nIdContactList, strComboItem, plugin );
    }

    /**
     * Increments hits in contact and contact_list_contact tables
     * 
     * @param nIdContactList
     *            id of the contactList
     * @param nIdContact
     *            id of the contact
     * @param plugin
     *            the plugin Contact
     */
    public static void updateHits( int nIdContactList, int nIdContact, Plugin plugin )
    {
        _dao.updateHits( nIdContactList, nIdContact, plugin );
    }

    // Contact order Management

    /**
     * Search the order number of contacts for one list
     * 
     * @return int the id by a given order
     * @param nIdContactList
     *            the id of the contactList
     * @param nContactOrder
     *            the number of order of the contact
     * @param plugin
     *            The Plugin object
     */
    public static int getContactIdByOrder( int nContactOrder, int nIdContactList, Plugin plugin )
    {
        return _dao.selectContactIdByOrder( nContactOrder, nIdContactList, plugin );
    }

    /**
     * returns the order of a contact in a list using its Id
     * 
     * @return int the id by a given order
     * @param nIdContact
     *            the id of the contact
     * @param nIdContactList
     *            the id of the contactList
     * @param plugin
     *            The Plugin object
     */
    public static int getContactOrderById( int nIdContact, int nIdContactList, Plugin plugin )
    {
        return _dao.selectContactOrderById( nIdContact, nIdContactList, plugin );
    }

    /**
     * Update the number order of contact
     * 
     * @param nIdContactList
     *            the id of the contactList
     * @param nNewOrder
     *            the new number of order
     * @param nId
     *            the Identifier of contact
     * @param plugin
     *            The Plugin object
     */
    public static void updateContactOrder( int nNewOrder, int nId, int nIdContactList, Plugin plugin )
    {
        _dao.storeContactOrder( nNewOrder, nId, nIdContactList, plugin );
    }
}
