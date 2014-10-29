package kz.arta.synergy.components.client.table;

import java.util.ArrayList;
import java.util.List;

/**
 * User: vsl
 * Date: 23.09.14
 * Time: 15:06
 */
public class TableTestUtils {
    public static User createUser(int i) {
        return new User("jon" + i, "jones" + i, "" + (85281 + i));
    }

    public static List<User> createUserList(int size, int userStart) {
        List<User> users = new ArrayList<User>(size);
        for (int i = 0; i < size; i++) {
            users.add(createUser(userStart + i));
        }
        return users;
    }
}
