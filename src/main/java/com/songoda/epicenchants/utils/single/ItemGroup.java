package com.songoda.epicenchants.utils.single;

import com.songoda.third_party.com.cryptomorin.xseries.XMaterial;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import org.apache.commons.lang.StringUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import static com.songoda.third_party.com.cryptomorin.xseries.XMaterial.*;

public class ItemGroup {

    private final Multimap<Group, XMaterial> groupMap;

    public ItemGroup() {
        this.groupMap = HashMultimap.create();

        this.groupMap.putAll(ItemGroup.Group.AXES, Arrays.asList(NETHERITE_AXE, DIAMOND_AXE, GOLDEN_AXE, IRON_AXE, STONE_AXE, WOODEN_AXE));

        this.groupMap.putAll(ItemGroup.Group.PICKAXES, Arrays.asList(NETHERITE_PICKAXE, DIAMOND_PICKAXE, GOLDEN_PICKAXE, IRON_PICKAXE, STONE_PICKAXE, WOODEN_PICKAXE));

        this.groupMap.putAll(ItemGroup.Group.HOES, Arrays.asList(NETHERITE_HOE, DIAMOND_HOE, GOLDEN_HOE, IRON_HOE, STONE_HOE, WOODEN_HOE));

        this.groupMap.putAll(ItemGroup.Group.SHOVELS, Arrays.asList(NETHERITE_SHOVEL, DIAMOND_SHOVEL, GOLDEN_SHOVEL, IRON_SHOVEL, STONE_SHOVEL, WOODEN_SHOVEL));

        this.groupMap.putAll(ItemGroup.Group.SWORDS, Arrays.asList(NETHERITE_SWORD, DIAMOND_SWORD, GOLDEN_SWORD, IRON_SWORD, STONE_SWORD, WOODEN_SWORD));

        this.groupMap.put(ItemGroup.Group.BOWS, BOW);

        this.groupMap.putAll(ItemGroup.Group.BOOTS, Arrays.asList(NETHERITE_BOOTS, DIAMOND_BOOTS, GOLDEN_BOOTS, IRON_BOOTS, LEATHER_BOOTS));

        this.groupMap.putAll(ItemGroup.Group.LEGGINGS, Arrays.asList(NETHERITE_LEGGINGS, DIAMOND_LEGGINGS, GOLDEN_LEGGINGS, IRON_LEGGINGS, LEATHER_LEGGINGS));

        this.groupMap.putAll(ItemGroup.Group.CHESTPLATES, Arrays.asList(NETHERITE_CHESTPLATE, DIAMOND_CHESTPLATE, GOLDEN_CHESTPLATE, IRON_CHESTPLATE, LEATHER_CHESTPLATE));

        this.groupMap.putAll(ItemGroup.Group.HELMETS, Arrays.asList(NETHERITE_HELMET, DIAMOND_HELMET, GOLDEN_HELMET, IRON_HELMET, LEATHER_HELMET));

        this.groupMap.put(ItemGroup.Group.TRIDENTS, TRIDENT);
    }

    public Set<XMaterial> get(String key) {
        Optional<Group> optionalGroup = Group.from(key);
        Set<XMaterial> output = new HashSet<>();

        optionalGroup.ifPresent(group -> output.addAll(getMaterials(group)));

        Optional<XMaterial> material = XMaterial.matchXMaterial(key);
        material.ifPresent(output::add);

        return output;
    }

    public boolean isValid(XMaterial material) {
        for (Group group : this.groupMap.keys()) {
            if (getMaterials(group).contains(material)) {
                return true;
            }
        }
        return false;
    }

    public Set<String> getGroups(Set<XMaterial> materials) {
        Set<String> groups = new HashSet<>();

        for (int i = 0; i < 5; i++) {
            getGroup(materials).ifPresent(group -> {
                groups.add(group.getName());
                new ArrayList<>(getMaterials(group)).forEach(materials::remove);
            });
        }

        groups.addAll(materials.stream().map(XMaterial::toString).collect(Collectors.toSet()));
        return groups;
    }

    public Optional<Group> getGroup(Set<XMaterial> materials) {
        for (Group group : Group.values()) {
            if (!group.getChildren().isEmpty() && group.getChildren().stream().allMatch(child -> materials.containsAll(this.groupMap.get(child)))) {
                return Optional.of(group);
            }
        }

        for (Map.Entry<Group, Collection<XMaterial>> entry : this.groupMap.asMap().entrySet()) {
            if (materials.containsAll(entry.getValue())) {
                return Optional.of(entry.getKey());
            }
        }
        return Optional.empty();
    }

    public Set<XMaterial> getMaterials(Group group) {
        Set<XMaterial> out = new HashSet<>();

        for (int i = 0; i < 5; i++) {
            if (group.getChildren().isEmpty()) {
                out.addAll(this.groupMap.get(group));
            } else {
                out.addAll(group.getChildren().stream().map(this::getMaterials).flatMap(Collection::stream).collect(Collectors.toSet()));
            }
        }

        return out;
    }

    public enum Group {
        AXES,
        HOES,
        PICKAXES,
        SHOVELS,
        TOOLS(AXES, PICKAXES, HOES, SHOVELS),

        SWORDS,
        BOWS,
        TRIDENTS,
        WEAPONS(SWORDS, BOWS, AXES, TRIDENTS),

        BOOTS,
        LEGGINGS,
        CHESTPLATES,
        HELMETS,
        ARMOR(BOOTS, LEGGINGS, CHESTPLATES, HELMETS);

        private final Set<Group> children;

        Group(Group... child) {
            this.children = child == null ? Collections.emptySet() : new HashSet<>(Arrays.asList(child));
        }

        public static Optional<Group> from(String key) {
            for (Group group : values()) {
                if (group.toString().equalsIgnoreCase(key)) {
                    return Optional.of(group);
                }
            }
            return Optional.empty();
        }

        public String getName() {
            return StringUtils.capitalize(toString().toLowerCase());
        }

        public Set<Group> getChildren() {
            return this.children;
        }
    }
}
