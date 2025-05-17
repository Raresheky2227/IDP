import React, { useState } from "react";

interface Props {
    onAdd: (title: string, description: string, file: File) => Promise<void>;
}

const AddEventForm: React.FC<Props> = ({ onAdd }) => {
    const [title, setTitle] = useState("");
    const [description, setDescription] = useState("");
    const [file, setFile] = useState<File | null>(null);
    const [error, setError] = useState<string | null>(null);
    const [success, setSuccess] = useState<string | null>(null);

    async function handleSubmit(e: React.FormEvent) {
        e.preventDefault();
        setError(null);
        setSuccess(null);
        if (!file) {
            setError("Please upload a PDF file.");
            return;
        }
        try {
            await onAdd(title, description, file);
            setSuccess("Event added successfully!");
            setTitle("");
            setDescription("");
            setFile(null);
        } catch (err: any) {
            setError("Add event failed: " + (err.response?.data || err.message));
        }
    }

    return (
        <form onSubmit={handleSubmit}>
            <h3>Add Event (VIP only)</h3>
            <label>
                Title
                <input value={title} onChange={e => setTitle(e.target.value)} required />
            </label>
            <label>
                Description
                <textarea value={description} onChange={e => setDescription(e.target.value)} required />
            </label>
            <label>
                PDF file
                <input type="file" accept="application/pdf" onChange={e => setFile(e.target.files?.[0] || null)} required />
            </label>
            <button type="submit">Add Event</button>
            {error && <div className="error">{error}</div>}
            {success && <div className="success">{success}</div>}
        </form>
    );
};

export default AddEventForm;
