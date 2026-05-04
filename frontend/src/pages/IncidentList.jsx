import { useEffect, useState } from "react";
import API from "../services/api";

// IncidentList page — shows all incidents in a table
// Java Developer 2 — Day 3

function IncidentList() {
  // State variables
  const [incidents, setIncidents] = useState([]);  // list of incidents
  const [loading, setLoading] = useState(true);    // show spinner while loading
  const [error, setError] = useState(null);         // show error if API fails

  // Fetch incidents when page loads
  useEffect(() => {
    fetchIncidents();
  }, []);

  const fetchIncidents = async () => {
    try {
      setLoading(true);
      setError(null);
      const response = await API.get("/incidents/all");
      setIncidents(response.data.content || []);
    } catch (err) {
      console.error("Failed to fetch incidents:", err);
      setError("Failed to load incidents. Please try again.");
    } finally {
      setLoading(false);
    }
  };

  // Severity badge color
  const getSeverityColor = (severity) => {
    switch (severity) {
      case "CRITICAL": return "bg-red-100 text-red-800";
      case "HIGH":     return "bg-orange-100 text-orange-800";
      case "MEDIUM":   return "bg-yellow-100 text-yellow-800";
      case "LOW":      return "bg-green-100 text-green-800";
      default:         return "bg-gray-100 text-gray-800";
    }
  };

  // Status badge color
  const getStatusColor = (status) => {
    switch (status) {
      case "OPEN":        return "bg-blue-100 text-blue-800";
      case "IN_PROGRESS": return "bg-purple-100 text-purple-800";
      case "RESOLVED":    return "bg-green-100 text-green-800";
      case "CLOSED":      return "bg-gray-100 text-gray-800";
      default:            return "bg-gray-100 text-gray-800";
    }
  };

  // ── Loading state ─────────────────────────────────────────────
  if (loading) {
    return (
      <div className="flex justify-center items-center h-64">
        <div className="text-center">
          <div className="animate-spin rounded-full h-12 w-12 border-b-2 border-blue-600 mx-auto"></div>
          <p className="mt-4 text-gray-600">Loading incidents...</p>
        </div>
      </div>
    );
  }

  // ── Error state ───────────────────────────────────────────────
  if (error) {
    return (
      <div className="flex justify-center items-center h-64">
        <div className="text-center">
          <p className="text-red-600 font-medium">{error}</p>
          <button
            onClick={fetchIncidents}
            className="mt-4 px-4 py-2 bg-blue-600 text-white rounded hover:bg-blue-700"
          >
            Try Again
          </button>
        </div>
      </div>
    );
  }

  // ── Empty state ───────────────────────────────────────────────
  if (incidents.length === 0) {
    return (
      <div className="flex justify-center items-center h-64">
        <div className="text-center">
          <p className="text-gray-500 text-lg">No incidents found.</p>
          <p className="text-gray-400 text-sm mt-2">
            Create your first incident to get started.
          </p>
        </div>
      </div>
    );
  }

  // ── Main table ────────────────────────────────────────────────
  return (
    <div className="p-6">
      <div className="flex justify-between items-center mb-6">
        <h1 className="text-2xl font-bold text-gray-800">Incidents</h1>
        <span className="text-sm text-gray-500">
          Total: {incidents.length}
        </span>
      </div>

      <div className="overflow-x-auto bg-white rounded-lg shadow">
        <table className="min-w-full divide-y divide-gray-200">
          <thead className="bg-gray-50">
            <tr>
              <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase">ID</th>
              <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase">Title</th>
              <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase">Type</th>
              <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase">Severity</th>
              <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase">Status</th>
              <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase">Reported By</th>
              <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase">Date</th>
            </tr>
          </thead>
          <tbody className="bg-white divide-y divide-gray-200">
            {incidents.map((incident) => (
              <tr key={incident.id} className="hover:bg-gray-50">
                <td className="px-6 py-4 text-sm text-gray-500">#{incident.id}</td>
                <td className="px-6 py-4 text-sm font-medium text-gray-900">
                  {incident.title}
                </td>
                <td className="px-6 py-4 text-sm text-gray-500">
                  {incident.incidentType}
                </td>
                <td className="px-6 py-4">
                  <span className={`px-2 py-1 text-xs font-medium rounded-full ${getSeverityColor(incident.severity)}`}>
                    {incident.severity}
                  </span>
                </td>
                <td className="px-6 py-4">
                  <span className={`px-2 py-1 text-xs font-medium rounded-full ${getStatusColor(incident.status)}`}>
                    {incident.status}
                  </span>
                </td>
                <td className="px-6 py-4 text-sm text-gray-500">
                  {incident.reportedBy}
                </td>
                <td className="px-6 py-4 text-sm text-gray-500">
                  {new Date(incident.incidentDate).toLocaleDateString()}
                </td>
              </tr>
            ))}
          </tbody>
        </table>
      </div>
    </div>
  );
}

export default IncidentList;
