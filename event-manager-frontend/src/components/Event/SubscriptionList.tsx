import React from "react";

interface Props {
    subscriptions: string[];
}

const SubscriptionList: React.FC<Props> = ({ subscriptions }) => (
    <>
        <h3>My Subscribed Events</h3>
        {subscriptions.length === 0
            ? <div>You have no subscriptions.</div>
            : <ul>
                {subscriptions.map(subId => (
                    <li key={subId}>Event ID: {subId}</li>
                ))}
            </ul>
        }
    </>
);

export default SubscriptionList;
