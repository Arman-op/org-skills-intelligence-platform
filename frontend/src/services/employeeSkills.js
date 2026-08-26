import { apiFetch } from './platformApi';

export const fetchSkills = async () => {
  try {
    const response = await apiFetch('/skills');
    if (!response.ok) throw new Error('Failed to fetch skills');
    return await response.json();
  } catch (err) {
    console.error('fetchSkills error', err);
    return [];
  }
};

export const fetchEmployeeSkills = async (email) => {
  if (!email) return [];
  try {
    const response = await apiFetch(`/user-skills?email=${encodeURIComponent(email)}`);
    if (!response.ok) throw new Error('Failed to fetch employee skills');
    return await response.json();
  } catch (err) {
    console.error('fetchEmployeeSkills error', err);
    return [];
  }
};

export const saveEmployeeSkill = async (payload) => {
  try {
    const response = await apiFetch('/user-skills', {
      method: 'POST',
      body: JSON.stringify(payload),
    });
    if (!response.ok) throw new Error('Failed to save employee skill');
    return await response.json();
  } catch (err) {
    console.error('saveEmployeeSkill error', err);
    return null;
  }
};

export const updateEmployeeSkill = async (id, payload) => {
  if (!id) return null;
  try {
    const response = await apiFetch(`/user-skills/${id}`, {
      method: 'PUT',
      body: JSON.stringify(payload),
    });
    if (!response.ok) throw new Error('Failed to update employee skill');
    return await response.json();
  } catch (err) {
    console.error('updateEmployeeSkill error', err);
    return null;
  }
};
