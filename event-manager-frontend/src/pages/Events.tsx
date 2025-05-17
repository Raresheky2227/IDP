import React, { useEffect, useState } from "react";
import { useAuthContext } from "../context/AuthContext";
import { listEvents, addEvent, deleteEvent } from "../api/events";
import EventList from "../components/Event/EventList";
import AddEventForm from "../components/Event/AddEventForm";
import Loader from "../components/Common/Loader";

const Events: React.FC = () => {
    const { token, user } = useAuthContext();
    const [events, setEvents] = useState<any[]>([]);
    const [loading, setLoading] = useState(false);
    const [error, setError] = useState<string | null>(null);

    async function refresh() {
        if (!token) return;
        setLoading(true);
        setError(null);
        try {
            const data = await listEvents(token);
            setEvents(data);
        } catch (err: any) {
            setError("Failed to load events.");
        }
        setLoading(false);
    }

    useEffect(() => { refresh(); }, [token]);

    async function handleAdd(title: string, description: string, file: File) {
        if (!token) return;
        await addEvent(token, title, description, file);
        await refresh();
    }

    async function handleDelete(id: string) {
        if (!token) return;
        if (!window.confirm("Are you sure?")) return;
        await deleteEvent(token, id);
        await refresh();
    }

    return (
        <>
            <h1>Events</h1>
            {user && user.roles.includes("ROLE_VIP") && (
                <AddEventForm onAdd={handleAdd} />
            )}
            {loading
                ? <Loader />
                : error
                    ? <div className="error">{error}</div>
                    : <EventList events={events} />
            }
            {user && user.roles.includes("ROLE_VIP") &&
                events.map(event =>
                    <button key={event.id} onClick={() => handleDelete(event.id)} style={{ marginRight: 10 }}>
                        Delete Event #{event.id}
                    </button>
                )
            }
        </>
    );
};

export default Events;
