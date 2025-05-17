// To keep it simple, just provide a download/view link for now.
import React from "react";

interface Props {
    pdfUrl: string;
}

const PdfViewer: React.FC<Props> = ({ pdfUrl }) => (
    <div style={{ margin: "1rem 0" }}>
        <a href={pdfUrl} target="_blank" rel="noopener noreferrer">View/Download PDF</a>
    </div>
);

export default PdfViewer;
