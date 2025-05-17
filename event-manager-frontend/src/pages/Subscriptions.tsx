import React, { useEffect, useState } from "react";
import { useAuthContext } from "../context/AuthContext";
import { listSubscriptions } from "../api/events";
import Loader from "../components/Common/Loader";
import SubscriptionList from "../components/Event/SubscriptionList";

const Subscriptions: React.FC = () => {
    const { token } = useAuthContext();
    const [subs, setSubs] = useState<string[]>([]);
    const [loading, setLoading] = useState(false);

    useEffect(() => {
        if (!token) return;
        setLoading(true);
        listSubscriptions(token).then(setSubs).finally(() => setLoading(false));
    }, [token]);

    return (
        <>
            {loading ? <Loader /> : <SubscriptionList subscriptions={subs} />}
        </>
    );
};
export default Subscriptions;
