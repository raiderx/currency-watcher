package org.karpukhin.currencywatcher.dao;

import org.karpukhin.currencywatcher.model.Notification;

import java.util.Collection;

/**
 * @author Pavel Karpukhin
 * @since 03.09.15
 */
public interface NotificationsDao {

    Collection<Notification> getNotifications();
}
