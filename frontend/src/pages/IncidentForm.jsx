import { useState } from "react";
import API from "../services/api";

// Incident Create/Edit Form
// Java Developer 2 — Day 4

function IncidentForm({ onSuccess }) {
  const [form, setForm] = useState({
    title: "",
    description: "",
    incidentType: "",
    severity: "MEDIUM",
    status: "OPEN",
    reportedBy: "",
    assignedTo: "",
    location: "",
    incidentDate: new Date().toISOString().slice(0, 16),
  });

  const [error, setError]   = useState("");
  const [loading, setLoading] = useState(false);
  const [success, setSuccess] = useState(false);

  const handleChange = (e) => {
    setForm({ ...form, [e.target.name]: e.target.value });
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setError("");

    // Basic validation
    if (!form.title || !form.description || !form.incidentType || !form.reportedBy) {
      setError("Please fill in all required fields");
      return;
    }

    try {
      setLoading(true);
      await API.post("/incidents/create", {
        ...form,
        incidentDate: new Date(form.incidentDate).toISOString(),
      });
      setSuccess(true);
      if (onSuccess) onSuccess();
    } catch (err) {
      setError(err.response?.data?.message || "Failed to create incident");
    } finally {
      setLoading(false);
    }
  };

  if (success) {
    return (
      <div className="text-center p-8">
        <p className="text-green-600 font-medium text-lg">
          ✅ Incident created successfully!
        </p>
        <button
          onClick={() => setSuccess(false)}
          className="mt-4 px-4 py-2 bg-blue-600 text-white rounded hover:bg-blue-700"
        >
          Create Another
        </button>
      </div>
    );
  }

  return (
    <div className="max-w-2xl mx-auto bg-white rounded-lg shadow p-6">
      <h2 className="text-xl font-bold text-gray-800 mb-6">
        Report New Incident
      </h2>

      {error && (
        <div className="bg-red-50 text-red-700 px-4 py-3 rounded mb-4 text-sm">
          {error}
        </div>
      )}

      <form onSubmit={handleSubmit} className="space-y-4">

        {/* Title */}
        <div>
          <label className="block text-sm font-medium text-gray-700 mb-1">
            Title <span className="text-red-500">*</span>
          </label>
          <input
            type="text"
            name="title"
            value={form.title}
            onChange={handleChange}
            placeholder="Brief title of the incident"
            className="w-full border border-gray-300 rounded px-3 py-2 text-sm focus:outline-none focus:ring-2 focus:ring-blue-500"
          />
        </div>

        {/* Description */}
        <div>
          <label className="block text-sm font-medium text-gray-700 mb-1">
            Description <span className="text-red-500">*</span>
          </label>
          <textarea
            name="description"
            value={form.description}
            onChange={handleChange}
            placeholder="Describe what happened..."
            rows={4}
            className="w-full border border-gray-300 rounded px-3 py-2 text-sm focus:outline-none focus:ring-2 focus:ring-blue-500"
          />
        </div>

        {/* Type and Severity in a row */}
        <div className="grid grid-cols-2 gap-4">
          <div>
            <label className="block text-sm font-medium text-gray-700 mb-1">
              Incident Type <span className="text-red-500">*</span>
            </label>
            <input
              type="text"
              name="incidentType"
              value={form.incidentType}
              onChange={handleChange}
              placeholder="e.g. Security Breach"
              className="w-full border border-gray-300 rounded px-3 py-2 text-sm focus:outline-none focus:ring-2 focus:ring-blue-500"
            />
          </div>
          <div>
            <label className="block text-sm font-medium text-gray-700 mb-1">
              Severity
            </label>
            <select
              name="severity"
              value={form.severity}
              onChange={handleChange}
              className="w-full border border-gray-300 rounded px-3 py-2 text-sm focus:outline-none focus:ring-2 focus:ring-blue-500"
            >
              <option value="LOW">Low</option>
              <option value="MEDIUM">Medium</option>
              <option value="HIGH">High</option>
              <option value="CRITICAL">Critical</option>
            </select>
          </div>
        </div>

        {/* Reported By and Assigned To */}
        <div className="grid grid-cols-2 gap-4">
          <div>
            <label className="block text-sm font-medium text-gray-700 mb-1">
              Reported By <span className="text-red-500">*</span>
            </label>
            <input
              type="text"
              name="reportedBy"
              value={form.reportedBy}
              onChange={handleChange}
              placeholder="Your name"
              className="w-full border border-gray-300 rounded px-3 py-2 text-sm focus:outline-none focus:ring-2 focus:ring-blue-500"
            />
          </div>
          <div>
            <label className="block text-sm font-medium text-gray-700 mb-1">
              Assigned To
            </label>
            <input
              type="text"
              name="assignedTo"
              value={form.assignedTo}
              onChange={handleChange}
              placeholder="Investigator name"
              className="w-full border border-gray-300 rounded px-3 py-2 text-sm focus:outline-none focus:ring-2 focus:ring-blue-500"
            />
          </div>
        </div>

        {/* Date and Location */}
        <div className="grid grid-cols-2 gap-4">
          <div>
            <label className="block text-sm font-medium text-gray-700 mb-1">
              Incident Date
            </label>
            <input
              type="datetime-local"
              name="incidentDate"
              value={form.incidentDate}
              onChange={handleChange}
              className="w-full border border-gray-300 rounded px-3 py-2 text-sm focus:outline-none focus:ring-2 focus:ring-blue-500"
            />
          </div>
          <div>
            <label className="block text-sm font-medium text-gray-700 mb-1">
              Location
            </label>
            <input
              type="text"
              name="location"
              value={form.location}
              onChange={handleChange}
              placeholder="Where it happened"
              className="w-full border border-gray-300 rounded px-3 py-2 text-sm focus:outline-none focus:ring-2 focus:ring-blue-500"
            />
          </div>
        </div>

        <button
          type="submit"
          disabled={loading}
          className="w-full bg-blue-700 text-white py-2 rounded font-medium hover:bg-blue-800 disabled:opacity-50"
        >
          {loading ? "Creating..." : "Create Incident"}
        </button>
      </form>
    </div>
  );
}

export default IncidentForm;
