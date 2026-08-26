import { apiFetch } from './platformApi';

export const enrollInProgram = async (payload) => {
  try {
    const res = await apiFetch('/enrollments', {
      method: 'POST',
      body: JSON.stringify(payload),
    });
    if (!res.ok) throw new Error('Enroll failed');
    return await res.json();
  } catch (err) {
    console.error(err);
    return null;
  }
};

export const fetchEnrollments = async (email) => {
  try {
    const q = email ? `?email=${encodeURIComponent(email)}` : '';
    const res = await apiFetch(`/enrollments${q}`);
    if (!res.ok) throw new Error('Fetch enrollments failed');
    return await res.json();
  } catch (err) {
    console.error(err);
    return [];
  }
};
