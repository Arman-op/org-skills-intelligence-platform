import { apiFetch } from './platformApi';

const API_BASE = '/employee-improvements';

const toSummaryText = (items, emptyLabel) => {
  if (!items.length) {
    return emptyLabel;
  }

  return items.map((item) => item.title || item.skill || item).join(' | ');
};

export const buildImprovementPayload = ({ user, examResults, enrolledCourses = [] }) => {
  const skillEntries = Object.entries(examResults || {});
  const gaps = skillEntries.filter(([, value]) => value.level !== 'strong');
  const scoreValues = skillEntries.map(([, value]) => value.score || 0);
  const overallScore = scoreValues.length
    ? Math.round(scoreValues.reduce((sum, score) => sum + score, 0) / scoreValues.length)
    : 0;

  return {
    employeeEmail: user.email || 'unknown@company.com',
    employeeName: user.name || 'Employee',
    role: user.role || user.accountType || 'Employee',
    targetRole: user.targetRole || user.examRole || '',
    overallScore,
    gapSummary: toSummaryText(
      gaps.map(([skill, value]) => `${skill} (${value.score}%)`),
      'No active gaps detected',
    ),
    enrolledCourses: toSummaryText(enrolledCourses, 'No courses enrolled yet'),
    improvementSummary: enrolledCourses.length
      ? `Employee has enrolled in ${enrolledCourses.length} gap-focused learning resource(s).`
      : 'Assessment completed. Learning resources recommended for weak skills.',
  };
};

export const saveEmployeeImprovement = async (payload) => {
  try {
    const response = await apiFetch(API_BASE, {
      method: 'POST',
      body: JSON.stringify(payload),
    });

    if (!response.ok) {
      throw new Error('Failed to save employee improvement');
    }

    return await response.json();
  } catch (error) {
    console.error(error);
    return null;
  }
};

export const fetchEmployeeImprovements = async () => {
  try {
    const response = await apiFetch(API_BASE);
    if (!response.ok) {
      throw new Error('Failed to fetch employee improvements');
    }

    return await response.json();
  } catch (error) {
    console.error(error);
    return [];
  }
};