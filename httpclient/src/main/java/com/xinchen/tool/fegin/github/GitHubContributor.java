package com.xinchen.tool.fegin.github;

import feign.Feign;
import feign.Param;
import feign.RequestLine;
import feign.gson.GsonDecoder;

import java.util.List;

/**
 * 查询贡献者以及提交issue
 *
 * curl https://api.github.com/repos/OpenFeign/feign/contributors -k
 *
 * @author Xin Chen (xinchenmelody@gmail.com)
 * @version 1.0
 * @date Created In 2020/8/3 22:59
 */
public class GitHubContributor {
    interface GitHub {
        @RequestLine("GET /repos/{owner}/{repo}/contributors")
        List<Contributor> contributors(@Param("owner") String owner, @Param("repo") String repo);

        @RequestLine("POST /repos/{owner}/{repo}/issues")
        void createIssue(Issue issue, @Param("owner") String owner, @Param("repo") String repo);

    }

    static class Contributor{
        String login;
        int contributions;
    }

    static class Issue{
        String title;
        String body;
        List<String> assignees;
        int milestone;
        List<String> labels;
    }

    public static void main(String[] args) {
        GitHub github = Feign.builder()
                .decoder(new GsonDecoder())
                .target(GitHub.class, "https://api.github.com");

        // Fetch and print a list of the contributors to this library.
        github.contributors("OpenFeign", "feign")
                .forEach((contributor-> System.out.println(contributor.login + " (" + contributor.contributions + ")")));
    }
}
