import axios from "axios";

const API = process.env.REACT_APP_API_URL + "/api/events";

export async function listEvents(token: string) {
    const res = await axios.get(API, {
        headers: { Authorization: `Bearer ${token}` },
    });
    return res.data;
}

export async function addEvent(token: string, title: string, description: string, file: File) {
    const formData = new FormData();
    formData.append(
        "event",
        new Blob([JSON.stringify({ title, description })], { type: "application/json" })
    );
    formData.append("file", file);

    const res = await axios.post(API, formData, {
        headers: {
            Authorization: `Bearer ${token}`,
            "Content-Type": "multipart/form-data"
        }
    });
    return res.data;
}

export async function deleteEvent(token: string, id: string) {
    await axios.delete(`${API}/${id}`, {
        headers: { Authorization: `Bearer ${token}` },
    });
}

export async function subscribeEvent(token: string, id: string) {
    await axios.post(`${API}/${id}/subscribe`, {}, {
        headers: { Authorization: `Bearer ${token}` },
    });
}

export async function unsubscribeEvent(token: string, id: string) {
    await axios.delete(`${API}/${id}/unsubscribe`, {
        headers: { Authorization: `Bearer ${token}` },
    });
}

export async function listSubscriptions(token: string) {
    const res = await axios.get(`${API}/subscriptions`, {
        headers: { Authorization: `Bearer ${token}` },
    });
    return res.data;
}

export function getPdfUrl(token: string, id: string) {
    return `${API}/${id}/pdf?token=${token}`;
}
