export class DiscordChannel {

    id: string;
    name: string;
    category: string;
    label: string;

    constructor(id: string, name: string, category: string, label: string) {
        this.id = id;
        this.name = name;
        this.category = category;
        this.label = label;
    }
}