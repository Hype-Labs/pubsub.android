package hypelabs.com.hypepubsub;

import org.junit.Test;

import java.security.NoSuchAlgorithmException;

public class ClientsListUnitTest
{
    @Test
    public void testListOperations() throws NoSuchAlgorithmException
    {
        byte CLIENT_ID1[] = new byte[] {(byte) 0x01, (byte) 0x02, (byte) 0x03, (byte) 0x04, (byte) 0x05, (byte) 0x06, (byte) 0x07, (byte) 0x08, (byte) 0x09, (byte) 0x10, (byte) 0x11, (byte) 0x12};
        byte CLIENT_ID2[] = new byte[] {(byte) 0x12, (byte) 0x01, (byte) 0x02, (byte) 0x03, (byte) 0x04, (byte) 0x05, (byte) 0x06, (byte) 0x07, (byte) 0x08, (byte) 0x09, (byte) 0x10, (byte) 0x11};
        byte CLIENT_ID3[] = new byte[] {(byte) 0x11, (byte) 0x12, (byte) 0x01, (byte) 0x02, (byte) 0x03, (byte) 0x04, (byte) 0x05, (byte) 0x06, (byte) 0x07, (byte) 0x08, (byte) 0x09, (byte) 0x10};
        byte CLIENT_ID4[] = new byte[] {(byte) 0x10, (byte) 0x11, (byte) 0x12, (byte) 0x01, (byte) 0x02, (byte) 0x03, (byte) 0x04, (byte) 0x05, (byte) 0x06, (byte) 0x07, (byte) 0x08, (byte) 0x09};

        // Test the creation of a client's list
        ClientsList clients = new ClientsList();
        /*
        CU_ASSERT_PTR_NOT_NULL_FATAL(clients);
        CU_ASSERT_PTR_NULL(clients->head);
        CU_ASSERT(clients->size == 0);

        // Add 4 clients to the list
        hpb_list_clients_add(clients, CLIENT_ID3);
        hpb_list_clients_add(clients, CLIENT_ID2);
        hpb_list_clients_add(clients, CLIENT_ID4);
        hpb_list_clients_add(clients, CLIENT_ID1);

        // Validate that the clients are inserted in the right order
        LinkedListIterator *it = linked_list_iterator_create(clients);
        aux_cl = (HpbClient *) linked_list_iterator_get_element(it);
        CU_ASSERT_NSTRING_EQUAL(aux_cl->id, CLIENT_ID3, HPB_ID_BYTE_SIZE);
        linked_list_iterator_advance(it);
        aux_cl = (HpbClient *) linked_list_iterator_get_element(it);
        CU_ASSERT_NSTRING_EQUAL(aux_cl->id, CLIENT_ID2, HPB_ID_BYTE_SIZE);
        linked_list_iterator_advance(it);
        aux_cl = (HpbClient *) linked_list_iterator_get_element(it);
        CU_ASSERT_NSTRING_EQUAL(aux_cl->id, CLIENT_ID4, HPB_ID_BYTE_SIZE);
        linked_list_iterator_advance(it);
        aux_cl = (HpbClient *) linked_list_iterator_get_element(it);
        CU_ASSERT_NSTRING_EQUAL(aux_cl->id, CLIENT_ID1, HPB_ID_BYTE_SIZE);
        CU_ASSERT(clients->size == 4);

        // Remove the client which is the header of the list and validate
        // that the list is correctly modified
        hpb_list_clients_remove(clients, CLIENT_ID3);
        linked_list_iterator_reset(it);
        aux_cl = (HpbClient *) linked_list_iterator_get_element(it);
        CU_ASSERT_NSTRING_EQUAL(aux_cl->id, CLIENT_ID2, HPB_ID_BYTE_SIZE);
        linked_list_iterator_advance(it);
        aux_cl = (HpbClient *) linked_list_iterator_get_element(it);
        CU_ASSERT_NSTRING_EQUAL(aux_cl->id, CLIENT_ID4, HPB_ID_BYTE_SIZE);
        linked_list_iterator_advance(it);
        aux_cl = (HpbClient *) linked_list_iterator_get_element(it);
        CU_ASSERT_NSTRING_EQUAL(aux_cl->id, CLIENT_ID1, HPB_ID_BYTE_SIZE);
        CU_ASSERT(clients->size == 3);

        // Remove client that was already removed and validate that nothing
        // happens
        hpb_list_clients_remove(clients, CLIENT_ID3);
        CU_ASSERT(clients->size == 3);

        // Remove another client and validate that the list is correctly
        // modified
        hpb_list_clients_remove(clients, CLIENT_ID4);
        linked_list_iterator_reset(it);
        aux_cl = (HpbClient *) linked_list_iterator_get_element(it);
        CU_ASSERT_NSTRING_EQUAL(aux_cl->id, CLIENT_ID2, HPB_ID_BYTE_SIZE);
        linked_list_iterator_advance(it);
        aux_cl = (HpbClient *) linked_list_iterator_get_element(it);
        CU_ASSERT_NSTRING_EQUAL(aux_cl->id, CLIENT_ID1, HPB_ID_BYTE_SIZE);
        CU_ASSERT(clients->size == 2);

        // Remove another client and validate that the list is correctly
        // modified
        hpb_list_clients_remove(clients, CLIENT_ID1);
        linked_list_iterator_reset(it);
        aux_cl = (HpbClient *) linked_list_iterator_get_element(it);
        CU_ASSERT_NSTRING_EQUAL(aux_cl->id, CLIENT_ID2, HPB_ID_BYTE_SIZE);
        CU_ASSERT(clients->size == 1);

        // Remove last client of the list
        hpb_list_clients_remove(clients, CLIENT_ID2);
        linked_list_iterator_reset(it);
        aux_cl = (HpbClient *) linked_list_iterator_get_element(it);
        CU_ASSERT_PTR_NULL(aux_cl);
        CU_ASSERT(clients->size == 0);

        // Add a client that was previously removed
        hpb_list_clients_add(clients, CLIENT_ID4);
        linked_list_iterator_reset(it);
        aux_cl = (HpbClient *) linked_list_iterator_get_element(it);
        CU_ASSERT_NSTRING_EQUAL(aux_cl->id, CLIENT_ID4, HPB_ID_BYTE_SIZE);
        CU_ASSERT(clients->size == 1);

        // Add all 4 new clients again. Client 4 was already inserted so
        // we validate that it is not duplicated.
        hpb_list_clients_add(clients, CLIENT_ID1);
        hpb_list_clients_add(clients, CLIENT_ID2);
        hpb_list_clients_add(clients, CLIENT_ID3);
        hpb_list_clients_add(clients, CLIENT_ID4);
        linked_list_iterator_reset(it);
        CU_ASSERT_NSTRING_EQUAL(aux_cl->id, CLIENT_ID4, HPB_ID_BYTE_SIZE);
        linked_list_iterator_advance(it);
        aux_cl = (HpbClient *) linked_list_iterator_get_element(it);
        CU_ASSERT_NSTRING_EQUAL(aux_cl->id, CLIENT_ID1, HPB_ID_BYTE_SIZE);
        linked_list_iterator_advance(it);
        aux_cl = (HpbClient *) linked_list_iterator_get_element(it);
        CU_ASSERT_NSTRING_EQUAL(aux_cl->id, CLIENT_ID2, HPB_ID_BYTE_SIZE);
        linked_list_iterator_advance(it);
        aux_cl = (HpbClient *) linked_list_iterator_get_element(it);
        CU_ASSERT_NSTRING_EQUAL(aux_cl->id, CLIENT_ID3, HPB_ID_BYTE_SIZE);
        CU_ASSERT(clients->size == 4);

        // Test find against a non-existent ID
        HLByte NON_EXISTENT_CLIENT_ID[] = new byte[] {(byte) 0x16, (byte) 0x11, (byte) 0x12, (byte) 0x01, (byte) 0x12, (byte) 0x03, (byte) 0x04, (byte) 0x05, (byte) 0x06, (byte) 0x07, (byte) 0x08, (byte) 0x09};
        aux_cl = hpb_list_clients_find(clients, NON_EXISTENT_CLIENT_ID);
        CU_ASSERT_PTR_NULL(aux_cl);

        // Test find against a existent IDs. Validate client's IDs and keys.
        HLByte CLIENT_KEY3[] = new byte[] {(byte) 0x9a, (byte) 0xc1, (byte) 0xb0, (byte) 0x41, (byte) 0x5e, (byte) 0x0a, (byte) 0x97, (byte) 0x73, (byte) 0x8c, (byte) 0x57, (byte) 0xe7, (byte) 0xe6, (byte) 0x3f, (byte) 0x68, (byte) 0x50, (byte) 0xab, (byte) 0x21, (byte) 0xe4, (byte) 0x7e, (byte) 0xb4};
        HLByte CLIENT_KEY4[] = new byte[] {(byte) 0x44, (byte) 0x20, (byte) 0x01, (byte) 0xf9, (byte) 0x64, (byte) 0xd9, (byte) 0xfe, (byte) 0x34, (byte) 0x9a, (byte) 0x5f, (byte) 0x30, (byte) 0x8a, (byte) 0xb1, (byte) 0x41, (byte) 0x15, (byte) 0x0e, (byte) 0x05, (byte) 0x5b, (byte) 0xe5, (byte) 0x46};
        aux_cl = hpb_list_clients_find(clients, CLIENT_ID3);
        CU_ASSERT_PTR_NOT_NULL(aux_cl);
        CU_ASSERT_NSTRING_EQUAL(aux_cl->id, CLIENT_ID3, HPB_ID_BYTE_SIZE);
        CU_ASSERT_NSTRING_EQUAL(aux_cl->key, CLIENT_KEY3, SHA1_BLOCK_SIZE);
        aux_cl = hpb_list_clients_find(clients, CLIENT_ID4);
        CU_ASSERT_PTR_NOT_NULL(aux_cl);
        CU_ASSERT_NSTRING_EQUAL(aux_cl->id, CLIENT_ID4, HPB_ID_BYTE_SIZE);
        CU_ASSERT_NSTRING_EQUAL(aux_cl->key, CLIENT_KEY4, SHA1_BLOCK_SIZE);

        */
    }
}