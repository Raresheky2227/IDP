import React from "react";
import { Link } from "react-router-dom";
import { Event } from "../../types";

const EventList: React.FC<{ events: Event[] }> = ({ events }) => (
    <>
        <h2>Events</h2>
        {events.length === 0 && <div>No events found.</div>}
        {events.map(event => (
            <div className="event-card" key={event.id}>
                <h3>{event.title}</h3>
                <div>{event.description}</div>
                <div>Event ID: {event.id}</div>
                <Link to={`/events/${event.id}`}>View Details</Link>
            </div>
        ))}
    </>
);

export default EventList;
