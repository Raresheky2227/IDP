import React, { useEffect, useState } from "react";
import { useParams } from "react-router-dom";
import { useAuthContext } from "../context/AuthContext";
import {
    subscribeEvent, unsubscribeEvent, listEvents, listSubscriptions
} from "../api/events";
import Loader from "../components/Common/Loader";

const EventDetail: React.FC = () => {
    const { id } = useParams<{ id: string }>();
    const { token, user } = useAuthContext();
    const [event, setEvent] = useState<any | null>(null);
    const [loading, setLoading] = useState(false);
    const [msg, setMsg] = useState<string | null>(null);

    const [subs, setSubs] = useState<string[]>([]);
    const [subsLoading, setSubsLoading] = useState(false);

    useEffect(() => {
        if (!token) return;
        setLoading(true);
        listEvents(token)
            .then(evts => {
                setEvent(evts.find((e: any) => String(e.id) === String(id)) || null);
                setLoading(false);
            });
    }, [token, id]);

    // Fetch subscriptions
    useEffect(() => {
        if (!token) return;
        setSubsLoading(true);
        listSubscriptions(token)
            .then(setSubs)
            .finally(() => setSubsLoading(false));
    }, [token]);

    async function handleSubscribe() {
        if (!token || !id) return;
        setMsg(null);
        try {
            await subscribeEvent(token, id);
            setMsg("Subscribed successfully!");
            // Update subs immediately
            setSubs(prev => [...prev, String(id)]);
        } catch {
            setMsg("Subscription failed.");
        }
    }

    async function handleUnsubscribe() {
        if (!token || !id) return;
        setMsg(null);
        try {
            await unsubscribeEvent(token, id);
            setMsg("Unsubscribed successfully!");
            setSubs(prev => prev.filter(eid => eid !== String(id)));
        } catch {
            setMsg("Unsubscription failed.");
        }
    }

    function handleDownloadPdf(eventId: string, token: string) {
        fetch(`${process.env.REACT_APP_API_URL}/api/events/${eventId}/pdf`, {
            headers: {
                Authorization: `Bearer ${token}`,
            }
        })
            .then(res => {
                if (!res.ok) throw new Error("Failed to download PDF");
                return res.blob();
            })
            .then(blob => {
                const url = window.URL.createObjectURL(blob);
                const a = document.createElement('a');
                a.href = url;
                a.download = `event_${eventId}.pdf`;
                a.click();
                window.URL.revokeObjectURL(url);
            })
            .catch(() => setMsg("Could not download PDF"));
    }

    if (loading || !event || subsLoading) return <Loader />;

    const isSubscribed = subs.includes(String(id));

    return (
        <>
            <h2>Event Details</h2>
            <div className="event-card">
                <h3>{event.title}</h3>
                <div>{event.description}</div>
                <div>Event ID: {event.id}</div>
                {token && (
                    <>
                        <button
                            onClick={() => handleDownloadPdf(event.id, token)}
                            style={{ margin: "1rem 0" }}
                        >
                            Download PDF
                        </button>
                        <div className="event-actions">
                            <div style={{ marginBottom: "0.5rem" }}>
                                {isSubscribed
                                    ? "Currently subscribed"
                                    : "Currently not subscribed"}
                            </div>
                            {isSubscribed ? (
                                <button onClick={handleUnsubscribe}>Unsubscribe</button>
                            ) : (
                                <button onClick={handleSubscribe}>Subscribe</button>
                            )}
                        </div>
                        {msg && <div>{msg}</div>}
                    </>
                )}
            </div>
        </>
    );
};

export default EventDetail;
