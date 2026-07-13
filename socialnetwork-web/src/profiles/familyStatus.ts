import type { FamilyStatus } from "./types";

export const FAMILY_STATUS_OPTIONS: { value: FamilyStatus; label: string }[] = [
    { value: "SINGLE", label: "Single" },
    { value: "IN_A_RELATIONSHIP", label: "In a relationship" },
    { value: "ENGAGED", label: "Engaged" },
    { value: "MARRIED", label: "Married" },
    { value: "COHABITATING", label: "Cohabitating" },
    { value: "SEPARATED", label: "Separated" },
    { value: "DIVORCED", label: "Divorced" },
    { value: "WIDOWED", label: "Widowed" },
];

export function formatFamilyStatus(status: FamilyStatus | null | undefined): string | null {
    return FAMILY_STATUS_OPTIONS.find((option) => option.value === status)?.label ?? null;
}
